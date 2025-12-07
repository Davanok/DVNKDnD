package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import kotlin.uuid.Uuid

fun CharacterMainEntityInfo.toDbCharacterMainEntity(characterId: Uuid) = DbCharacterMainEntity(
    characterId = characterId,
    entityId = entity.entity.id,
    subEntityId = subEntity?.entity?.id,
    level = level
)