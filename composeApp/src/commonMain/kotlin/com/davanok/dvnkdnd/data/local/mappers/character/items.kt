package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import kotlin.uuid.Uuid

fun CharacterItem.toDbCharacterItemLink(characterId: Uuid) = DbCharacterItemLink(
    characterId = characterId,
    itemId = item.entity.id,
    equipped = equipped,
    active = active,
    attuned = attuned,
    count = count
)