package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import kotlin.uuid.Uuid

@Immutable
data class CharacterWithAllModifiers(
    val character: CharacterBase,
    val proficiencyBonus: Int,
    val attributes: DnDAttributesGroup,

    val selectedModifiers: Set<Uuid>,
    val entities: List<DnDEntityWithModifiers>,
    val entityIdToLevel: Map<Uuid, Int>
)

@Immutable
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val modifiersGroups: List<DnDModifiersGroup>,
)

fun DnDFullEntity.toEntityWithModifiers() = DnDEntityWithModifiers(
    entity = toDnDEntityMin(),
    modifiersGroups = modifiersGroups
)