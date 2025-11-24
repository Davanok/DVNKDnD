package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.ui.components.toSignedSpacedString
import org.jetbrains.compose.resources.stringResource

@Composable
fun FullSpellAttack.buildString(level: Int = 0) = buildString {
    val modified = modifiers.lastOrNull { it.level <= level }
    append(modified?.diceCount ?: diceCount)
    append(' ')
    append(stringResource((modified?.dice ?: dice).stringRes))
    val appendValue = modified?.modifier ?: modifier
    if (appendValue != 0) {
        append(' ')
        append(appendValue.toSignedSpacedString())
    }
}

@Composable
fun FullSpell.buildAttacksString(level: Int) = buildString {
    val dices = mutableMapOf<Dices, Int>()
    var appendValue = 0
    attacks.forEach { attack ->
        val modified = attack.modifiers.lastOrNull { it.level <= level }
        val dice = modified?.dice ?: attack.dice
        var dicesCount = dices.getOrElse(dice) { 0 }

        dicesCount += (modified?.diceCount ?: attack.diceCount)

        dices[dice] = dicesCount

        appendValue += (modified?.modifier ?: attack.modifier)
    }

    dices.toList().sortedByDescending { it.first.ordinal }.forEach { (dice, count) ->
        append(count)
        append(' ')
        append(stringResource(dice.stringRes))
    }
    if (appendValue != 0) {
        append(' ')
        append(appendValue.toSignedSpacedString())
    }
}