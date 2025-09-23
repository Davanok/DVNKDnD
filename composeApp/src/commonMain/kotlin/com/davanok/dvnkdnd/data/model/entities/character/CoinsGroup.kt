package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import kotlinx.serialization.Serializable

@Serializable
data class CoinsGroup(
    val copper: Int,
    val silver: Int,
    val electrum: Int,
    val gold: Int,
    val platinum: Int
)
fun CharacterCoins.toDnDCoinsGroup() = CoinsGroup(
    copper = copper,
    silver = silver,
    electrum = electrum,
    gold = gold,
    platinum = platinum
)