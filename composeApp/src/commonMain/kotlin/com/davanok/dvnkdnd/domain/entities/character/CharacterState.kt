package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CharacterState(
    val state: DnDFullEntity,
    val source: DnDFullEntity?,
    val deletable: Boolean
)
@Serializable
data class CharacterStateLink(
    val stateId: Uuid,
    val sourceId: Uuid?,
    val deletable: Boolean
)

fun CharacterState.toCharacterStateLink() = CharacterStateLink(
    stateId = state.entity.id,
    sourceId = source?.entity?.id,
    deletable = deletable
)
