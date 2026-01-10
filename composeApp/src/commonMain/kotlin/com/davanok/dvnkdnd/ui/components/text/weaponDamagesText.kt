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
    append(stringResource(Res.string.damage))
    append(": ")

    val groupedDamages = damages.groupBy { it.condition }

    val sortedKeys = groupedDamages.keys.sortedBy { it != null }

    sortedKeys.forEachIndexed { index, condition ->
        val group = groupedDamages[condition] ?: return@forEachIndexed

        if (index > 0) append(" + ")

        val dices = mutableMapOf<Dices, Int>()
        var appendValue = 0

        group.forEach { damage ->
            val dicesCount = dices.getOrElse(damage.dice) { 0 }
            dices[damage.dice] = dicesCount + damage.diceCount
            appendValue += damage.modifier
        }

        dices.toList().sortedByDescending { it.first.ordinal }.forEachIndexed { i, (dice, count) ->
            if (i > 0) append(' ')
            if (count > 1) {
                append(count)
                append(' ')
            }
            append(stringResource(dice.stringRes))
        }

        if (appendValue != 0 || dices.isEmpty()) {
            append(' ')
            append(appendValue.toSignedSpacedString())
        }

        if (condition != null) {
            append(" (")
            append(stringResource(condition.type.stringRes))
            if (condition.target != null) {
                append(condition.target)
            }
            append(")")
        }
    }
}