package com.davanok.dvnkdnd.domain.dnd

import com.davanok.dvnkdnd.domain.enums.dndEnums.CasterProgression
import com.davanok.dvnkdnd.domain.entities.dndEntities.ArmorInfo

object DnDConstants {
    const val MAX_VALUE_TO_BUY = 15
    const val MIN_VALUE_TO_BUY = 8
    const val BUYING_BALANCE = 27
    val DEFAULT_ARRAY = intArrayOf(15, 14, 13, 12, 10, 8)

    val MULTICLASS_SPELL_SLOTS_BY_LEVEL = arrayOf(
        intArrayOf(2),   // 1
        intArrayOf(3),   // 2
        intArrayOf(4,2),   // 3
        intArrayOf(4,3),   // 4
        intArrayOf(4,3,2),   // 5
        intArrayOf(4,3,3),   // 6
        intArrayOf(4,3,3,1),   // 7
        intArrayOf(4,3,3,2),   // 8
        intArrayOf(4,3,3,3,1),   // 9
        intArrayOf(4,3,3,3,2),   // 10
        intArrayOf(4,3,3,3,2,1),   // 11
        intArrayOf(4,3,3,3,2,1),   // 12
        intArrayOf(4,3,3,3,2,1,1),   // 13
        intArrayOf(4,3,3,3,2,1,1),   // 14
        intArrayOf(4,3,3,3,2,1,1,1),   // 15
        intArrayOf(4,3,3,3,2,1,1,1),   // 16
        intArrayOf(4,3,3,3,2,1,1,1,1),   // 17
        intArrayOf(4,3,3,3,3,1,1,1,1),   // 18
        intArrayOf(4,3,3,3,3,2,1,1,1),   // 19
        intArrayOf(4,3,3,3,3,2,2,1,1)    // 20
    )
}

fun calculateModifier(attributeValue: Int) = (attributeValue - 10).floorDiv(2)

fun pointBuyCost(score: Int): Int = when {
    score <= 8 -> 0
    score >= 14 -> score * 2 - 21
    else -> score - 8
}

fun calculateBuyingModifiersSum(scores: List<Int>) =
    scores.sumOf { pointBuyCost(it) }

fun proficiencyBonusByLevel(level: Int): Int =
    level / 5 + 2

fun calculateArmorClass(dexterity: Int, armor: ArmorInfo?): Int {
    val dexterityModifier = calculateModifier(dexterity)
    if (armor == null) return 10 + dexterityModifier
    if (armor.dexMaxModifier == null) return armor.armorClass + dexterityModifier
    return armor.armorClass + dexterityModifier.coerceAtMost(armor.dexMaxModifier)
}

fun resolveCasterSpellSlotsContribution(level: Int, caster: CasterProgression) = when (caster) {
    CasterProgression.FULL -> level
    CasterProgression.HALF_PLUS -> (level + 1) / 2
    CasterProgression.HALF -> level / 2
    CasterProgression.THIRD -> level / 3
    CasterProgression.NONE, CasterProgression.OTHER -> 0
}

fun calculateSpellDifficultyClass(proficiencyBonus: Int, attributeValue: Int) =
    8 + proficiencyBonus + calculateModifier(attributeValue)