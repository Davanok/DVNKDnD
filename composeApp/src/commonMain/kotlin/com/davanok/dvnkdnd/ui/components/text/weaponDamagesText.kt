package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.domain.entities.dndEntities.WeaponDamageInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import com.davanok.dvnkdnd.ui.components.toSignedSpacedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.damage
import dvnkdnd.composeapp.generated.resources.if_value
import org.jetbrains.compose.resources.stringResource

@Composable
fun buildDamagesString(damages: List<WeaponDamageInfo>) = buildString {
    append(stringResource(Res.string.damage))
    append(": ")
    val groupedByType = damages.groupBy { it.damageType }.toList()

    groupedByType.forEachIndexed { typeIndex, (_, typeGroup) ->
        if (typeIndex > 0) append(" + ")
        val groupedByCondition = typeGroup
            .groupBy { it.condition }
            .toList()
            .sortedBy { it.first != null }

        groupedByCondition.forEachIndexed { condIndex, (condition, damageEntries) ->
            if (condIndex > 0) append(" + ")
            val diceCounts = mutableMapOf<Dices, Int>()
            var flatModifier = 0

            damageEntries.forEach { damage ->
                diceCounts[damage.dice] = diceCounts.getOrElse(damage.dice) { 0 } + damage.diceCount
                flatModifier += damage.modifier
            }
            val sortedDice = diceCounts.toList().sortedByDescending { it.first.ordinal }

            sortedDice.forEachIndexed { i, (dice, count) ->
                if (i > 0) append(" + ")

                if (count > 1) {
                    append(count)
                    append(' ')
                }
                append(stringResource(dice.stringRes))
            }

            if (flatModifier != 0)
                append(flatModifier.toSignedSpacedString())

            condition?.let {
                append(" (")
                append(stringResource(Res.string.if_value))
                append(' ')
                append(it.type)
                if (it.target != null) {
                    append(": ")
                    append(it.target)
                }
                append(')')
            }
        }
    }
}

@Composable
fun FullWeapon.buildDamagesString() = buildDamagesString(damages)