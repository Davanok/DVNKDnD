package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CustomModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifierExtendedInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import kotlin.uuid.Uuid


/**
 * Aggregates all active and custom modifiers, resolves their dynamic values,
 * and groups them by their target type.
 * * The resulting map is sorted by [ModifierExtendedInfo.priority] in ascending order
 * for each [DnDModifierTargetType].
 * * @return A map of target types to their corresponding prioritized list of resolved modifiers.
 */
fun CharacterFull.calculateModifiers(): Map<DnDModifierTargetType, List<ModifierExtendedInfo>> {
    val allModifiers = mutableListOf<ModifierExtendedInfo>()

    // 1. Collect and map Entity Modifiers
    entities.flatMap { entity ->
        entity.modifiersGroups.flatMap { group ->
            group.modifiers
                .filter { it.id in selectedModifiers }
                .map { modifier ->
                    mapToExtendedInfo(
                        group = group,
                        modifier = modifier,
                        entityId = entity.entity.id
                    )
                }
        }
    }.let { allModifiers.addAll(it) }

    // 2. Collect and map Custom Modifiers
    customModifiers
        .map { mapCustomToExtendedInfo(it) }
        .let { allModifiers.addAll(it) }

    // 3. Group by target and sort by priority
    return allModifiers
        .groupBy { it.targetGlobal }
        .mapValues { (_, list) -> list.sortedBy { it.priority } }
}

/**
 * Maps standard entity-based modifiers into the extended UI-ready model.
 */
private fun CharacterFull.mapToExtendedInfo(
    group: ModifiersGroup,
    modifier: DnDModifier,
    entityId: Uuid
) = ModifierExtendedInfo(
    id = modifier.id,
    isCustom = false,
    priority = group.priority,
    groupId = group.id,
    name = group.name,
    targetGlobal = group.target,
    target = modifier.target,
    operation = group.operation,
    valueSource = group.valueSource,
    valueSourceTarget = group.valueSourceTarget,
    value = group.value,
    state = UiSelectableState(selectable = modifier.selectable, selected = true),
    resolvedValue = resolveValueSource(
        group.valueSource,
        group.valueSourceTarget,
        entityId,
        group.value
    )
)

/**
 * Maps manual/custom modifiers into the extended UI-ready model.
 */
private fun CharacterFull.mapCustomToExtendedInfo(
    custom: CustomModifier
) = ModifierExtendedInfo(
    id = custom.id,
    isCustom = true,
    priority = custom.priority,
    groupId = null,
    name = custom.name,
    targetGlobal = custom.targetGlobal,
    target = custom.target,
    operation = custom.operation,
    valueSource = custom.valueSource,
    valueSourceTarget = custom.valueSourceTarget,
    value = custom.value,
    state = UiSelectableState(selectable = false, selected = true),
    resolvedValue = resolveValueSource(
        source = custom.valueSource,
        valueSourceTarget = custom.valueSourceTarget,
        entityId = null,
        modifierValue = custom.value
    )
)