package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import com.davanok.dvnkdnd.ui.components.toSignedSpacedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.damage
import org.jetbrains.compose.resources.stringResource

@Composable
fun FullWeapon.buildDamagesString() = buildString {
    val dices = mutableMapOf<Dices, Int>()
    var appendValue = 0
    damages.forEach { damage ->
        var dicesCount = dices.getOrElse(damage.dice) { 0 }

        dicesCount += damage.diceCount

        dices[damage.dice] = dicesCount

        appendValue += damage.modifier
    }

    append(stringResource(Res.string.damage))
    append(": ")
    dices.toList().sortedByDescending { it.first.ordinal }.forEach { (dice, count) ->
        if (count > 1) {
            append(count)
            append(' ')
        }
        append(stringResource(dice.stringRes))
    }
    if (appendValue != 0) {
        append(' ')
        append(appendValue.toSignedSpacedString())
    }
}