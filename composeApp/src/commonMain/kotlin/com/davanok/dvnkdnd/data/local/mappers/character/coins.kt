package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCoins
import kotlin.uuid.Uuid


fun DbCharacterCoins.toCoinsGroup() = CoinsGroup(
    copper = copper,
    silver = silver,
    electrum = electrum,
    gold = gold,
    platinum = platinum
)

fun CoinsGroup.toDbCharacterCoins(characterId: Uuid) = DbCharacterCoins(
    characterId = characterId,
    copper = copper,
    silver = silver,
    electrum = electrum,
    gold = gold,
    platinum = platinum
)