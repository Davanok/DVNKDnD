package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.core.utils.apply
import com.davanok.dvnkdnd.core.utils.asEnum
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroupInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.SkillsGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import kotlin.uuid.Uuid

/**
 * Aggregates and resolves all active and custom modifiers, applying them sequentially
 * so later modifiers can depend on previous ones. Results are grouped by target scope.
 *
 * The modifiers list is processed in ascending order of [DnDValueModifier.priority].
 */
fun CharacterFull.calculateValueModifiers(): Map<ModifierValueTarget, List<ValueModifierInfo>> {
    // 1. Gather and sort all modifiers by priority
    val allRawModifiers = collectRawModifiers().sortedBy { it.modifier.priority }

    // 2. Initialize running contexts (mutable) with base values
    val currentAttributes = attributes.toMap().toMutableMap()
    val currentSkills = currentAttributes.toAttributesGroup().toSkillsGroup().toMap().toMutableMap()

    // 3. Sequential resolution: each modifier sees the latest context
    val resolvedResults = allRawModifiers.map { wrapper ->
        wrapper.toResolvedInfo(
            attributesContext = currentAttributes.toAttributesGroup(),
            skillsContext = currentSkills.toSkillsGroup(),
            resolveValueSource = ::resolveValueSource
        ).also {
            // 4. Immediately update running context when modifier targets an attribute or skill
            applySingleModifierToContext(it, currentAttributes, currentSkills)
        }
    }

    // 5. Group for UI
    return resolvedResults.groupBy { it.modifier.targetScope }
}

/** Lightweight wrapper for modifiers (entity-provided or custom). */
private data class RawModifierWrapper(
    val modifier: DnDValueModifier,
    val group: ModifiersGroupInfo,
    val entityId: Uuid?, // null for custom modifiers
    val isSelectable: Boolean,
    val isSelected: Boolean
)

private fun CharacterFull.collectRawModifiers(): List<RawModifierWrapper> {
    val entityModifiers = entities.flatMap { entity ->
        entity.modifiersGroups.flatMap { group ->
            group.modifiers
                .filterIsInstance<DnDValueModifier>()
                .filter { it.id in selectedModifiers } // only active/selected
                .map { modifier ->
                    RawModifierWrapper(
                        modifier = modifier,
                        group = ModifiersGroupInfo(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            selectionLimit = group.selectionLimit
                        ),
                        entityId = entity.entity.id,
                        isSelectable = group.selectionLimit <= 0,
                        isSelected = modifier.id in selectedModifiers.valueModifiers
                    )
                }
        }
    }

    val customModifiers = customModifiers
        .filterIsInstance<CharacterCustomValueModifier>()
        .map { it.toDnDValueModifierWrapper(selectedModifiers.valueModifiers) }

    return entityModifiers + customModifiers
}

/** Convert a custom modifier to the wrapper form used by the pipeline. */
private fun CharacterCustomValueModifier.toDnDValueModifierWrapper(selectedModifiers: Set<Uuid>) =
    RawModifierWrapper(
        modifier = toDnDValueModifier(),
        group = ModifiersGroupInfo(
            id = id,
            name = name,
            description = description,
            selectionLimit = 0
        ),
        entityId = null,
        isSelectable = false,
        isSelected = id in selectedModifiers
    )

/** Resolve a single wrapper into a ValueModifierInfo using the injected contexts. */
private fun RawModifierWrapper.toResolvedInfo(
    attributesContext: AttributesGroup?,
    skillsContext: SkillsGroup?,
    resolveValueSource: (ValueSourceType, String?, Uuid?) -> Int
): ValueModifierInfo {
    val sourceValue = resolveValueSourceWithContext(
        source = modifier.sourceType,
        valueSourceKey = modifier.targetKey,
        entityId = entityId,
        attributesContext = attributesContext,
        skillsContext = skillsContext,
        resolveValueSource = resolveValueSource
    )

    val resolved = (sourceValue * modifier.multiplier).toInt() + modifier.flatValue

    return ValueModifierInfo(
        modifier = modifier,
        group = group,
        resolvedValue = resolved,
        state = UiSelectableState(selectable = isSelectable, selected = isSelected)
    )
}

/**
 * Resolves a value source, preferring the provided contexts (attributes/skills) if available.
 * Falls back to the legacy resolver when context is absent.
 */
private fun resolveValueSourceWithContext(
    source: ValueSourceType,
    valueSourceKey: String?,
    entityId: Uuid?,
    attributesContext: AttributesGroup?,
    skillsContext: SkillsGroup?,
    resolveValueSource: (ValueSourceType, String?, Uuid?) -> Int
): Int {
    // Prefer context values to avoid recursion and reflect "latest" state.
    val fromContext: Int? = when (source) {
        ValueSourceType.ATTRIBUTE -> valueSourceKey?.asEnum<Attributes>()
            ?.let { attributesContext?.get(it) }

        ValueSourceType.ATTRIBUTE_MODIFIER -> valueSourceKey?.asEnum<Attributes>()
            ?.let { attr -> attributesContext?.let { calculateModifier(it[attr]) } }

        ValueSourceType.SKILL_MODIFIER -> valueSourceKey?.asEnum<Skills>()
            ?.let { skillsContext?.get(it) }

        else -> null
    }

    return fromContext ?: resolveValueSource(source, valueSourceKey, entityId)
}

/**
 * Applies a single resolved modifier to the running attribute/skill contexts.
 * Only attributes and skills feed back into subsequent resolutions.
 */
private fun applySingleModifierToContext(
    info: ValueModifierInfo,
    attributes: MutableMap<Attributes, Int>,
    skills: MutableMap<Skills, Int>
) {
    val targetKey = info.modifier.targetKey ?: return

    when (info.modifier.targetScope) {
        ModifierValueTarget.ATTRIBUTE -> targetKey.asEnum<Attributes>()?.let { attr ->
            val current = attributes[attr] ?: 0
            attributes[attr] = info.modifier.operation.apply(current, info.resolvedValue)
        }

        ModifierValueTarget.SKILL -> targetKey.asEnum<Skills>()?.let { skill ->
            val current = skills[skill] ?: 0
            skills[skill] = info.modifier.operation.apply(current, info.resolvedValue)
        }

        else -> {
            // other targets (AC, Speed, etc.) do not feed back into attribute/skill contexts
        }
    }
}

/** Convert stored custom modifier entity to DnDValueModifier */
private fun CharacterCustomValueModifier.toDnDValueModifier() = DnDValueModifier(
    id = id,
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = condition
)
