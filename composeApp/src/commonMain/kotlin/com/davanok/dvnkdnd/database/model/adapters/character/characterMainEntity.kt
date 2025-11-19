package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.database.entities.character.DbCharacterMainEntity
import kotlin.uuid.Uuid

fun CharacterMainEntityInfo.toDbCharacterMainEntity(characterId: Uuid) = DbCharacterMainEntity(
    characterId = characterId,
    entityId = entity.entity.id,
    subEntityId = subEntity?.entity?.id,
    level = level
)