package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import kotlin.uuid.Uuid

@Immutable
data class CharacterWithAllModifiers(
    val character: CharacterShortInfo,
    val proficiencyBonus: Int,
    val characterStats: DnDAttributesGroup?,

    val selectedModifiers: List<Uuid>,
    val entities: List<DnDEntityWithModifiers>
)

@Immutable
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val modifiers: List<DnDModifiersGroup>,
)

fun DnDFullEntity.toEntityWithModifiers() = DnDEntityWithModifiers(
    entity = toDnDEntityMin(),
    modifiers = modifiers
)