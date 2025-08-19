package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.entities.character.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkill
import com.davanok.dvnkdnd.data.model.types.UiSelectableState
import kotlin.uuid.Uuid

class SkillsTableState(
    private val columns: List<DnDEntityWithSkills>,
    initialSelectedSkills: Set<Uuid>
) {
    private val selectedEntityIds = initialSelectedSkills.toMutableSet()
    private val skillToEntities: Map<Skills, List<DnDSkill>> =
        columns.fastFlatMap { it.skills }
            .groupBy { it.skill }

    private val entityIdToColumn: Map<Uuid, DnDEntityWithSkills> =
        columns.fastFlatMap { col -> col.skills.map { it.id to col } }
            .toMap()

    fun getDisplayItems(): Map<Skills, UiSelectableState> {
        return skillToEntities.mapValues { (_, entities) ->
            val selected = entities.fastAny { selectedEntityIds.contains(it.id) }

            val fixedSelection = entities.fastAny { !it.selectable }
            val selectable = !fixedSelection && entities.fastAny { ent ->
                if (ent.id in selectedEntityIds) return@fastAny true
                val column = entityIdToColumn[ent.id] ?: return@fastAny false
                val limit = column.selectionLimit
                val selectedCount = column.skills.count { selectedEntityIds.contains(it.id) }
                // Entity is selectable only if selection limit not yet reached or it's already selected
                (selectedCount < limit)
            }

            UiSelectableState(
                fixedSelection = fixedSelection,
                selected = selected,
                selectable = selectable
            )
        }
    }

    fun select(skill: Skills): Boolean {
        val entitiesAll = skillToEntities[skill].orEmpty()
        if (entitiesAll.isEmpty()) return false

        val alreadySelected = entitiesAll.fastAny { selectedEntityIds.contains(it.id) }
        if (alreadySelected) {
            var changed = false
            entitiesAll.fastForEach { ent ->
                if (selectedEntityIds.remove(ent.id)) changed = true
            }
            return changed
        }

        val toSelect = entitiesAll.fastFirstOrNull { ent ->
            if (!ent.selectable) return@fastFirstOrNull false
            val col = entityIdToColumn[ent.id] ?: return@fastFirstOrNull false
            val limit = col.selectionLimit
            val currentlySelectedInCol = col.skills.count { selectedEntityIds.contains(it.id) }
            currentlySelectedInCol < limit
        } ?: return false

        selectedEntityIds.add(toSelect.id)
        return true
    }

    fun getSelectedSkills(): Set<Uuid> = selectedEntityIds.toSet()

    fun validateSelectedSkills(): Boolean {
        return columns.fastAll { column ->
            val countSelected = column.skills.count { selectedEntityIds.contains(it.id) }
            column.selectionLimit == countSelected
        }
    }
}