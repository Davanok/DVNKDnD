package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import kotlin.random.Random

@Stable
class DiceRollerState internal constructor(
    private val onRolled: (List<Pair<ThrowSpec, List<Int>>>) -> Unit
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

    fun roll(specs: List<ThrowSpec>) {
        val results = specs.map { spec ->
            val maxValue = spec.dice.faces + 1
            spec to List(spec.count) { Random.nextInt(1, maxValue) }
        }

        animRequest = AnimRequest(results)
        _animationState.value = AnimationState.Idle
    }

    fun roll(dice: Dices, count: Int = 1, modifier: Any? = null) {
        roll(listOf(ThrowSpec(dice, count, modifier)))
    }
}

@Composable
fun rememberDiceRollerState(onRolled: (List<Pair<ThrowSpec, List<Int>>>) -> Unit): DiceRollerState =
    remember { DiceRollerState(onRolled) }
