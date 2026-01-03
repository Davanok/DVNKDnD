package com.davanok.dvnkdnd.core

import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Coins

object CoinsConverter {
    private const val COPPER_PER_SILVER = 10
    private const val COPPER_PER_ELECTRUM = 50
    private const val COPPER_PER_GOLD = 100
    private const val COPPER_PER_PLATINUM = 1000

    fun copperToSilver(copper: Int): Int =
        copper / COPPER_PER_SILVER

    fun copperToElectrum(copper: Int): Int =
        copper / COPPER_PER_ELECTRUM

    fun copperToGold(copper: Int): Int =
        copper / COPPER_PER_GOLD

    fun copperToPlatinum(copper: Int): Int =
        copper / COPPER_PER_PLATINUM

    fun copperToGroup(copper: Int): CoinsGroup {
        var rest = copper

        val platinum = rest / COPPER_PER_PLATINUM
        rest %= COPPER_PER_PLATINUM

        val gold = rest / COPPER_PER_GOLD
        rest %= COPPER_PER_GOLD

        val electrum = rest / COPPER_PER_ELECTRUM
        rest %= COPPER_PER_ELECTRUM

        val silver = rest / COPPER_PER_SILVER
        rest %= COPPER_PER_SILVER

        val copperLeft = rest

        return CoinsGroup(
            copper = copperLeft,
            silver = silver,
            electrum = electrum,
            gold = gold,
            platinum = platinum
        )
    }

    fun copperValue(coin: Coins): Int = when(coin) {
        Coins.COPPER -> 1
        Coins.SILVER -> COPPER_PER_SILVER
        Coins.ELECTRUM -> COPPER_PER_ELECTRUM
        Coins.GOLD -> COPPER_PER_GOLD
        Coins.PLATINUM -> COPPER_PER_PLATINUM
        Coins.OTHER -> 0
    }

    private fun Map<Coins, Int>.toCoinsGroup() = CoinsGroup(
        copper = get(Coins.COPPER) ?: 0,
        silver = get(Coins.SILVER) ?: 0,
        electrum = get(Coins.ELECTRUM) ?: 0,
        gold = get(Coins.GOLD) ?: 0,
        platinum = get(Coins.PLATINUM) ?: 0
    )

    fun copperToGroup(
        copper: Int,
        maxCoin: Coins
    ): CoinsGroup {
        val coins = Coins.entries
            .filter { it <= maxCoin && it != Coins.OTHER }
            .asReversed()

        var rest = copper

        val result = mutableMapOf<Coins, Int>()

        coins.forEach { coin ->
            val inCopper = copperValue(coin)
            val count = rest / inCopper
            rest %= inCopper

            result[coin] = count
        }

        return result.toCoinsGroup()
    }

    fun groupToCopper(group: CoinsGroup): Int =
        group.copper +
                group.silver * COPPER_PER_SILVER +
                group.electrum * COPPER_PER_ELECTRUM +
                group.gold * COPPER_PER_GOLD +
                group.platinum * COPPER_PER_PLATINUM
}