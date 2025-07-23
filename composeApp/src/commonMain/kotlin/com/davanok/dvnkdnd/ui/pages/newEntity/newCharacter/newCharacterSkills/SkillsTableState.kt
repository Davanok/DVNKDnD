package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.model.entities.DnDSkill
import kotlin.uuid.Uuid

data class UiSkillState(
    val selectable: Boolean,
    val selected: Boolean
)

class SkillsTableState(
    private val columns: List<DnDEntityWithSkills>,
    initialSelectedSkills: Set<Uuid>
) {
    private val selectedEntityIds = initialSelectedSkills.toMutableSet()
    private val skillToEntities: Map<Skills, List<DnDSkill>> =
        columns.fastFlatMap { it.skills }
            .groupBy { it.skill }

    fun getDisplayItems(): Map<Skills, UiSkillState> {
        return skillToEntities.mapValues { (_, entities) ->
            val selected = entities.fastAny { selectedEntityIds.contains(it.id) }

            val selectable = entities.fastAny { ent ->
                if (!ent.selectable) return@fastAny false
                val column = columns.firstOrNull { it.skills.contains(ent) } ?: return@fastAny false
                val limit = column.selectionLimit
                val selectedCount = column.skills.count { selectedEntityIds.contains(it.id) }
                // Entity is selectable only if selection limit not yet reached or it's already selected
                (limit == null || selectedCount < limit) && !selectedEntityIds.contains(ent.id)
            }

            UiSkillState(selectable = selectable, selected = selected)
        }
    }

    fun select(skill: Skills): Boolean {
        val entities = skillToEntities[skill].orEmpty().fastFilter { it.selectable }
        if (entities.isEmpty()) return false
        if (entities.fastAny { selectedEntityIds.contains(it.id) }) {
            entities.fastForEach { selectedEntityIds.remove(it.id) }
            return true
        }
        val toSelect = entities.fastFirstOrNull { ent ->
            val col = columns.fastFirstOrNull { column -> column.skills.contains(ent) } ?: return@fastFirstOrNull false
            val limit = col.selectionLimit
            val currentlySelectedInCol = col.skills.count { selectedEntityIds.contains(it.id) }
            limit == null || currentlySelectedInCol < limit
        } ?: return false
        selectedEntityIds.add(toSelect.id)
        return true
    }

    fun getSelectedSkills(): Set<Uuid> = selectedEntityIds.toSet()

    fun validateSelectedSkills(): Boolean {
        return columns.fastAll { column ->
            column.selectionLimit?.let { limit ->
                val countSelected = column.skills.count { selectedEntityIds.contains(it.id) }
                countSelected == limit
            } ?: true
        }
    }
}