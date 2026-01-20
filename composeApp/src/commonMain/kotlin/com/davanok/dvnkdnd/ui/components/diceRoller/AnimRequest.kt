package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Immutable

@Immutable
data class AnimRequest(
    val dices: List<Pair<ThrowSpec, List<Int>>>
) {
    init {
        require(dices.isNotEmpty())
        require(dices.all { it.second.isNotEmpty() })
    }
}

enum class AnimationState { Idle, Running, Finished }
