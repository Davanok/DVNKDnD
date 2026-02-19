package com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers

import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroupInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.CharacterMovementType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedValuesTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlin.uuid.Uuid

fun CharacterFull.getBaseValueModifiers(): List<RawModifierWrapper> = buildList {
    addAll(Attributes.entries) { attribute ->
        createBaseRawWrapper(
            targetScope = ModifierValueTarget.ATTRIBUTE,
            targetKey = attribute.name,
            sourceType = ValueSourceType.FLAT,
            sourceKey = null,
            flatValue = attributes[attribute],
        )
    }

    addAll(Attributes.entries) { attribute ->
        createBaseRawWrapper(
            targetScope = ModifierValueTarget.ATTRIBUTE_THROW,
            targetKey = attribute.name,
            sourceType = ValueSourceType.ATTRIBUTE_MODIFIER,
            sourceKey = attribute.name
        )
    }

    addAll(Attributes.entries) { attribute ->
        createBaseRawWrapper(
            targetScope = ModifierValueTarget.SAVING_THROW,
            targetKey = attribute.name,
            sourceType = ValueSourceType.ATTRIBUTE_MODIFIER,
            sourceKey = attribute.name
        )
    }

    addAll(Skills.entries) { skill ->
        createBaseRawWrapper(
            targetScope = ModifierValueTarget.SKILL,
            targetKey = skill.name,
            sourceType = ValueSourceType.ATTRIBUTE_MODIFIER,
            sourceKey = skill.attribute.name
        )
    }
    add(createBaseRawWrapper(
        targetScope = ModifierValueTarget.DERIVED_STAT,
        targetKey = DnDModifierDerivedValuesTargets.ARMOR_CLASS.name,
        sourceType = ValueSourceType.ATTRIBUTE_MODIFIER,
        sourceKey = Attributes.DEXTERITY.name,
        flatValue = 10 // Base unarmored AC
    ))

    // 2. Base Initiative: Dexterity Modifier
    add(createBaseRawWrapper(
        targetScope = ModifierValueTarget.DERIVED_STAT,
        targetKey = DnDModifierDerivedValuesTargets.INITIATIVE.name,
        sourceType = ValueSourceType.ATTRIBUTE_MODIFIER,
        sourceKey = Attributes.DEXTERITY.name
    ))

    // 3. Base Passive Perception: 10 + Perception Skill
    add(createBaseRawWrapper(
        targetScope = ModifierValueTarget.DERIVED_STAT,
        targetKey = DnDModifierDerivedValuesTargets.PASSIVE_PERCEPTION.name,
        sourceType = ValueSourceType.SKILL_MODIFIER,
        sourceKey = Skills.PERCEPTION.name,
        flatValue = 10
    ))

    // 4. Base Speed (from Race)
    val baseSpeed = calculateBaseSpeed() // Utility in utils.kt
    add(createBaseRawWrapper(
        targetScope = ModifierValueTarget.SPEED,
        targetKey = CharacterMovementType.WALK.name,
        sourceType = ValueSourceType.FLAT,
        sourceKey = null,
        flatValue = baseSpeed
    ))
}

private fun createBaseRawWrapper(
    targetScope: ModifierValueTarget,
    targetKey: String?,
    sourceType: ValueSourceType,
    sourceKey: String?,
    flatValue: Int = 0,
) = RawModifierWrapper(
    isCustom = false,
    modifier = DnDValueModifier(
        id = Uuid.NIL,
        priority = Int.MIN_VALUE,
        targetScope = targetScope,
        targetKey = targetKey,
        operation = ValueOperation.SET,
        sourceType = sourceType,
        sourceKey = sourceKey,
        multiplier = 1.0,
        flatValue = flatValue,
        condition = null
    ),
    group = ModifiersGroupInfo(
        id = Uuid.NIL,
        name = "",
        description = null,
        selectionLimit = 0
    ),
    entityId = null,
    isSelectable = false,
    isSelected = true
)

private inline fun <T, E>MutableList<E>.addAll(values: Iterable<T>, transform: (T) -> E) =
    addAll(values.map(transform))