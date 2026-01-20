package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DiceRollerDefaults {
    const val MAIN_SPIN_DURATION_MS = 700
    const val SETTLE_SPRING_STIFFNESS = 900f
    const val SETTLE_SPRING_DAMPING = 1.0f

    val DEFAULT_MIN_ITEM_WIDTH: Dp = 150.dp
    val DEFAULT_CANVAS_SIZE: Dp = 150.dp


    @Composable
    fun DiceCompanionContent(state: AnimationState, value: Int, spec: ThrowSpec) {
        AnimatedVisibility(
            visible = state == AnimationState.Finished,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(value.toString(), modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}