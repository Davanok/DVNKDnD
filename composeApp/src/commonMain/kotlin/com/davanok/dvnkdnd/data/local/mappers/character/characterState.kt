package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterState
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterState
import com.davanok.dvnkdnd.domain.entities.character.CharacterStateLink
import kotlin.uuid.Uuid


fun DbJoinCharacterState.toCharacterState() = CharacterState(
    state = state.toDnDFullEntity(),
    source = source?.toDnDFullEntity(),
    deletable = link.deletable
)

fun CharacterStateLink.toDbCharacterStateLink(characterId: Uuid) = DbCharacterStateLink(
    characterId = characterId,
    stateId = stateId,
    sourceId = sourceId,
    deletable = deletable
)

fun CharacterState.toDbCharacterStateLink(characterId: Uuid) = DbCharacterStateLink(
    characterId = characterId,
    stateId = state.entity.id,
    sourceId = source?.entity?.id,
    deletable = deletable
)