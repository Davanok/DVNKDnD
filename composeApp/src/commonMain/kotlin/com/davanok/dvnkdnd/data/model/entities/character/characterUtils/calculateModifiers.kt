package com.davanok.dvnkdnd.data.model.entities.character.characterUtils

import androidx.compose.ui.util.fastFilteredMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.types.ModifierExtendedInfo
import com.davanok.dvnkdnd.data.model.ui.UiSelectableState

fun CharacterFull.calculateModifiers(): Map<DnDModifierTargetType, List<ModifierExtendedInfo>> {
    val result = mutableMapOf<DnDModifierTargetType, MutableList<Pair<Int, ModifierExtendedInfo>>>()
    entities.fastForEach { entity ->
        entity.modifiersGroups.fastForEach { group ->
            val modifiers = group.modifiers.fastFilteredMap(
                predicate = { it.id in selectedModifiers },
                transform = { modifier ->
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
            )
            result
                .getOrPut(group.target, ::mutableListOf)
                .addAll(modifiers)
        }
    }
    return result.mapValues { modifiers -> modifiers.value.sortedBy { it.first }.map { it.second } }
}