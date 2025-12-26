package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterMainEntities
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo

fun DbJoinCharacterMainEntities.toCharacterMainEntityInfo() = CharacterMainEntityInfo(
    level = link.level,
    entity = entity.toDnDFullEntity(),
    subEntity = subEntity?.toDnDFullEntity()
)