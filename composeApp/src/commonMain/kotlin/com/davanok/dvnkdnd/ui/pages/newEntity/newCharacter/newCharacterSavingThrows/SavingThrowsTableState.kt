package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows

import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
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

    private val entityIdToColumn: Map<Uuid, DnDEntityWithSavingThrows> =
        columns.fastFlatMap { col -> col.savingThrows.map { it.id to col } }
            .toMap()

    fun getDisplayItems(): Map<Stats, UiSelectableState> {
        return savingThrowsToEntities.mapValues { (_, entities) ->
            val selected = entities.fastAny { selectedEntityIds.contains(it.id) }

            val fixedSelection = entities.fastAny { !it.selectable }
            val selectable = !fixedSelection && entities.fastAny { ent ->
                if (!ent.selectable) return@fastAny false
                if (ent.id in selectedEntityIds) return@fastAny true
                val column = entityIdToColumn[ent.id] ?: return@fastAny false
                val limit = column.selectionLimit
                val selectedCount = column.savingThrows.count { selectedEntityIds.contains(it.id) }
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

    fun select(stat: Stats): Boolean {
        val entitiesAll = savingThrowsToEntities[stat].orEmpty()
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