package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows

import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.character.DnDEntityWithSavingThrows
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSavingThrow
import com.davanok.dvnkdnd.data.model.types.UiSelectableState
import kotlin.uuid.Uuid

class SavingThrowsTableState(
    private val columns: List<DnDEntityWithSavingThrows>,
    initialSelectedSavingThrow: Set<Uuid>
) {
    private val selectedEntityIds = initialSelectedSavingThrow.toMutableSet()
    private val savingThrowsToEntities: Map<Stats, List<DnDSavingThrow>> =
        columns.fastFlatMap { it.savingThrows }
            .groupBy { it.stat }

    fun getDisplayItems(): Map<Stats, UiSelectableState> {
        return savingThrowsToEntities.mapValues { (_, entities) ->
            val selected = entities.fastAny { selectedEntityIds.contains(it.id) }

            val selectable = entities.fastAny { ent ->
                if (!ent.selectable) return@fastAny false
                val column = columns.firstOrNull { it.savingThrows.contains(ent) } ?: return@fastAny false
                val limit = column.selectionLimit
                val selectedCount = column.savingThrows.count { selectedEntityIds.contains(it.id) }
                // Entity is selectable only if selection limit not yet reached or it's already selected
                (selectedCount < limit) && !selectedEntityIds.contains(ent.id)
            }

            UiSelectableState(selectable = selectable, selected = selected)
        }
    }

    fun select(stat: Stats): Boolean {
        val entities = savingThrowsToEntities[stat].orEmpty().fastFilter { it.selectable }
        if (entities.isEmpty()) return false
        if (entities.fastAny { selectedEntityIds.contains(it.id) }) {
            entities.fastForEach { selectedEntityIds.remove(it.id) }
            return true
        }
        val toSelect = entities.fastFirstOrNull { ent ->
            val col = columns.fastFirstOrNull { column -> column.savingThrows.contains(ent) } ?: return@fastFirstOrNull false
            val limit = col.selectionLimit
            val currentlySelectedInCol = col.savingThrows.count { selectedEntityIds.contains(it.id) }
            currentlySelectedInCol < limit
        } ?: return false
        selectedEntityIds.add(toSelect.id)
        return true
    }

    fun getSelectedSavingThrows(): Set<Uuid> = selectedEntityIds.toSet()

    fun validateSelectedSavingThrows(): Boolean {
        return columns.fastAll { column ->
            val countSelected = column.savingThrows.count { selectedEntityIds.contains(it.id) }
            column.selectionLimit == countSelected
        }
    }
}