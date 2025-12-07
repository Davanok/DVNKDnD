package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifierExtendedInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.ui.model.UiSelectableState

fun CharacterFull.calculateModifiers(): Map<DnDModifierTargetType, List<ModifierExtendedInfo>> {
    val result = mutableMapOf<DnDModifierTargetType, MutableList<Pair<Int, ModifierExtendedInfo>>>()
    entities.forEach { entity ->
        entity.modifiersGroups.forEach { group ->
            val modifiers = group.modifiers
                .filter { it.id in selectedModifiers }
                .map { modifier ->
                    group.priority to ModifierExtendedInfo(
                        groupId = group.id,
                        groupName = group.name,
                        modifier = modifier,
                        operation = group.operation,
                        valueSource = group.valueSource,
                        value = group.value,
                        state = UiSelectableState(
                            selectable = modifier.selectable,
                            selected = true
                        ),
                        resolvedValue = resolveValueSource(
                            group.valueSource,
                            group.valueSourceTarget,
                            entity.entity.id,
                            group.value
                        )
                    )
                }
            result
                .getOrPut(group.target, ::mutableListOf)
                .addAll(modifiers)
        }
    }
    return result.mapValues { modifiers -> modifiers.value.sortedBy { it.first }.map { it.second } }
}