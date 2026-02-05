package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterMainEntities
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import kotlin.uuid.Uuid

fun CharacterMainEntityInfo.toDbCharacterMainEntity(characterId: Uuid) = DbCharacterMainEntity(
    characterId = characterId,
    entityId = entity.entity.id,
    subEntityId = subEntity?.entity?.id,
    level = level
)

fun DbJoinCharacterMainEntities.toCharacterMainEntityInfo() = CharacterMainEntityInfo(
    level = link.level,
    entity = entity.toDnDFullEntity(),
    subEntity = subEntity?.toDnDFullEntity()
)
fun CharacterMainEntityLink.toDbCharacterMainEntity(characterId: Uuid) = DbCharacterMainEntity(
    characterId = characterId,
    entityId = entityId,
    subEntityId = subEntityId,
    level = level
)