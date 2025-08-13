package com.davanok.dvnkdnd.data.model.util

object DnDConstants {
    const val MAX_VALUE_TO_BUY = 15
    const val MIN_VALUE_TO_BUY = 8
    const val BUYING_BALANCE = 27
    val DEFAULT_ARRAY = arrayOf(15, 14, 13, 12, 10, 8)
}

fun calculateModifier(modifier: Int) = (modifier - 10).floorDiv(2)

fun pointBuyCost(score: Int): Int = when {
    score <= 8 -> 0
    score >= 14 -> score * 2 - 21
    else -> score - 8
}

fun calculateBuyingModifiersSum(scores: List<Int>) =
    scores.sumOf { pointBuyCost(it) }

fun proficiencyBonusByLevel(level: Int): Int =
    level / 5 + 2