package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.database.entities.character.CharacterMainEntity
import kotlin.uuid.Uuid

fun CharacterMainEntityInfo.toCharacterMainEntity(characterId: Uuid) = CharacterMainEntity(
    characterId = characterId,
    entityId = entity.entity.id,
    subEntityId = subEntity?.entity?.id,
    level = level
)