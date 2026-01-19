package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices

@Immutable
data class AnimRequest(
    val dices: List<Pair<Dices, List<Int>>>
) {
    init {
        require(dices.isNotEmpty())
        require(dices.all { it.second.isNotEmpty() })
    }
}

enum class AnimationState { Idle, Running, Finished }
