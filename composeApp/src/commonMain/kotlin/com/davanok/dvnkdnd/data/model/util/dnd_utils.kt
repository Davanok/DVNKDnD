package com.davanok.dvnkdnd.data.model.util

object DnDConstants {
    const val MAX_VALUE_TO_BUY = 15
    const val MIN_VALUE_TO_BUY = 8
    const val BUYING_BALANCE = 27
}

fun calculateModifier(modifier: Int) = (modifier - 10).floorDiv(2)

/**
 * 9  -> 1
 *
 * 10 -> 2
 *
 * 11 -> 3
 *
 * 12 -> 4
 *
 * 13 -> 5
 *
 * 14 -> 7
 *
 * 15 -> 9
 */
fun calculateModifierAmount(modifier: Int) = when {
    modifier < 9 -> 0
    modifier < 14 -> modifier - 8
    else -> modifier - 7
}
fun calculateBuyingModifiersSum(modifiers: List<Int>) = modifiers.sumOf {
    calculateModifierAmount(it)
}