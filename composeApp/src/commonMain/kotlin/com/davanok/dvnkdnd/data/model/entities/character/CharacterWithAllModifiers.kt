package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import kotlin.uuid.Uuid

data class CharacterWithAllModifiers(
    val character: CharacterShortInfo,
    val characterStats: DnDModifiersGroup?,

    val selectedModifierBonuses: List<Uuid>,
    val entities: List<DnDEntityWithModifiers>
)

@Immutable
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val selectionLimit: Int,
    val modifiers: List<DnDModifierBonus>,
)

fun DnDFullEntity.toEntityWithModifiers() = DnDEntityWithModifiers(
    entity = toDnDEntityMin(),
    selectionLimit = selectionLimits?.modifiers?: modifierBonuses.size,
    modifiers = modifierBonuses
)