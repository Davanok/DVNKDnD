package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.enums.dndEnums.Coins
import kotlinx.serialization.Serializable

@Serializable
data class CoinsGroup(
    val copper: Int = 0,
    val silver: Int = 0,
    val electrum: Int = 0,
    val gold: Int = 0,
    val platinum: Int = 0
) {
    operator fun get(coin: Coins) = when(coin) {
        Coins.COPPER -> copper
        Coins.SILVER -> silver
        Coins.ELECTRUM -> electrum
        Coins.GOLD -> gold
        Coins.PLATINUM -> platinum
        Coins.OTHER -> 0
    }
}