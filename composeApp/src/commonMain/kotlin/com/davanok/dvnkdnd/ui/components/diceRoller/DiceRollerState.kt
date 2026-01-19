package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import kotlin.random.Random

@Stable
class DiceRollerState internal constructor(
    private val onRolled: (List<Pair<Dices, List<Int>>>) -> Unit
) {
    var animRequest by mutableStateOf<AnimRequest?>(null)
        private set

    private val _animationState = mutableStateOf(AnimationState.Idle)
    val animationState: AnimationState
        get() = _animationState.value

    internal fun setAnimationState(value: AnimationState) {
        _animationState.value = value
    }

    fun dismiss() {
        animRequest?.let { onRolled(it.dices) }
        animRequest = null
        _animationState.value = AnimationState.Idle
    }

    fun roll(dices: List<Pair<Dices, Int>>) {
        val diceValues = dices.fastMap { (dice, count) ->
            val maxValue = dice.faces + 1
            dice to List(count) { Random.nextInt(1, maxValue) }
        }

        animRequest = AnimRequest(
            dices = diceValues
        )
        _animationState.value = AnimationState.Idle
    }

    fun roll(dice: Dices, count: Int) = roll(listOf(dice to count))

    fun roll(dice: Dices) = roll(listOf(dice to 1))
}

@Composable
fun rememberDiceRollerState(onRolled: (List<Pair<Dices, List<Int>>>) -> Unit): DiceRollerState =
    remember { DiceRollerState(onRolled) }
