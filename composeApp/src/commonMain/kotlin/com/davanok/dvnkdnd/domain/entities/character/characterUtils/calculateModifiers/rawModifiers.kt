package com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers

import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroupInfo
import kotlin.uuid.Uuid

fun CharacterFull.collectRawValueModifiers(): List<RawModifierWrapper> =
    collectRawValueModifiers(
        entities = entities,
        selectedModifiers = selectedModifiers.valueModifiers,
        customModifiers = customModifiers.filterIsInstance<CharacterCustomValueModifier>()
    )

/**
 * aggregates all character value modifiers
 */
private fun collectRawValueModifiers(
    entities: List<DnDFullEntity>,
    selectedModifiers: Set<Uuid>,
    customModifiers: List<CharacterCustomValueModifier>
): List<RawModifierWrapper> {
    val entityModifiers = getEntityModifiers(entities, selectedModifiers)

    val customModifiers = customModifiers
        .map { it.toDnDValueModifierWrapper() }

    return entityModifiers + customModifiers
}

data class RawModifierWrapper(
    val isCustom: Boolean,
    val modifier: DnDValueModifier,
    val group: ModifiersGroupInfo,
    val entityId: Uuid?, // null for custom modifiers
    val isSelectable: Boolean,
    val isSelected: Boolean
)

private fun getEntityModifiers(
    entities: List<DnDFullEntity>,
    selectedModifiers: Set<Uuid>
) = entities
    .flatMap { entity ->
        entity.modifiersGroups.flatMap { group ->
            group.modifiers
                .filterIsInstance<DnDValueModifier>()
                .filter { it.id in selectedModifiers } // only active/selected
                .map { modifier ->
                    RawModifierWrapper(
                        isCustom = false,
                        modifier = modifier,
                        group = ModifiersGroupInfo(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            selectionLimit = group.selectionLimit
                        ),
                        entityId = entity.entity.id,
                        isSelectable = group.selectionLimit <= 0,
                        isSelected = true
                    )
                }
        }
    }

private fun CharacterCustomValueModifier.toDnDValueModifierWrapper() =
    RawModifierWrapper(
        isCustom = true,
        modifier = toDnDValueModifier(),
        group = ModifiersGroupInfo(
            id = id,
            name = name,
            description = description,
            selectionLimit = 0
        ),
        entityId = null,
        isSelectable = false,
        isSelected = true
    )

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