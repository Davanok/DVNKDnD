package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CoinsGroup
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCoins
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