package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSavingThrow
import kotlin.uuid.Uuid

data class CharacterWithAllSavingThrows(
    val character: CharacterShortInfo,
    val proficiencyBonus: Int,
    val stats: DnDModifiersGroup,

    val selectedSavingThrows: List<Uuid>,

    val entities: List<DnDEntityWithSavingThrows>
)

@Immutable
data class DnDEntityWithSavingThrows(
    val entity: DnDEntityMin,
    val selectionLimit: Int,
    val savingThrows: List<DnDSavingThrow>,
)

fun DnDFullEntity.toEntityWithSavingThrows() = DnDEntityWithSavingThrows(
    entity = toDnDEntityMin(),
    selectionLimit = selectionLimits?.savingThrows?: savingThrows.size,
    savingThrows = savingThrows
)