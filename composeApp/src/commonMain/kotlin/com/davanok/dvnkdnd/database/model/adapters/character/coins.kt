package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CoinsGroup
import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import kotlin.uuid.Uuid


fun CharacterCoins.toCoinsGroup() = CoinsGroup(
    copper = copper,
    silver = silver,
    electrum = electrum,
    gold = gold,
    platinum = platinum
)

fun CoinsGroup.toCharacterCoins(characterId: Uuid) = CharacterCoins(
    characterId = characterId,
    copper = copper,
    silver = silver,
    electrum = electrum,
    gold = gold,
    platinum = platinum
)