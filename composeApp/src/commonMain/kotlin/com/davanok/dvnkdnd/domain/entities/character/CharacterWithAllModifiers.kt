package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import kotlin.uuid.Uuid

@Immutable
data class CharacterWithAllModifiers(
    val character: CharacterBase,
    val attributes: AttributesGroup,

    val selectedModifiers: Set<Uuid>,
    val entities: List<DnDEntityWithModifiers>,
    val entityIdToLevel: Map<Uuid, Int>
)

@Immutable
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val modifiersGroups: List<ModifiersGroup>,
)

fun DnDFullEntity.toEntityWithModifiers() = DnDEntityWithModifiers(
    entity = toDnDEntityMin(),
    modifiersGroups = modifiersGroups
)