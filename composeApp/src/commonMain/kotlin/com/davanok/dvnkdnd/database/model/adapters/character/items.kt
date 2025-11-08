package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterItem
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink
import kotlin.uuid.Uuid

fun CharacterItem.toDbCharacterItemLink(characterId: Uuid) = DbCharacterItemLink(
    characterId = characterId,
    itemId = item.entity.id,
    equipped = equipped,
    attuned = attuned
)