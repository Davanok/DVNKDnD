package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.model.character.DbJoinCharacterItem
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem

fun DbJoinCharacterItem.toCharacterItem() = CharacterItem(
    equipped = link.equipped,
    active = link.active,
    attuned = link.attuned,
    count = link.count,
    item = item.toDnDFullEntity()
)