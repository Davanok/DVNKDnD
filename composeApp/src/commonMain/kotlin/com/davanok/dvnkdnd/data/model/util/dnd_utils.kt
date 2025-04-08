package com.davanok.dvnkdnd.data.model.util

object DnDConstants {
    const val MAX_VALUE_TO_BUY = 15
    const val MIN_VALUE_TO_BUY = 8
    const val BUYING_BALANCE = 27
}

fun calculateModifier(modifier: Int) = (modifier - 10).floorDiv(2)

/**
 * 9  -> 1
 * 10 -> 2
 * 11 -> 3
 * 12 -> 4
 * 13 -> 5
 * 14 -> 7
 * 15 -> 9
 */
fun calculateBuyingModifiersSum(modifiers: List<Int>): Int = modifiers.sumOf {
    when {
        it < 9 -> 0
        it < 14 -> it - 8
        else -> it - 7
    }
}