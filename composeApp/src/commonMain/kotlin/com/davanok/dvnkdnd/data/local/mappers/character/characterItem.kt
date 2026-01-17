package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterItem
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.character.CharacterItemLink
import kotlin.uuid.Uuid

fun DbJoinCharacterItem.toCharacterItem() = CharacterItem(
    equipped = link.equipped,
    attuned = link.attuned,
    count = link.count,
    item = item.toDnDFullEntity()
)
fun CharacterItemLink.toDbCharacterItemLink(characterId: Uuid) = DbCharacterItemLink(
    characterId = characterId,
    itemId = item,
    equipped = equipped,
    attuned = attuned,
    count = count
)