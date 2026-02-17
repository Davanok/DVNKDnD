package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun SwipeToActionBox(
    state: SwipeToDismissBoxState = rememberSwipeToDismissBoxState(),
    onDismiss: (SwipeToDismissBoxValue) -> Unit,
    actionIcon: @Composable () -> Unit,
    actionSurfaceColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) = SwipeToActionBox(
    state = state,
    onDismiss = onDismiss,
    startToEndIcon = actionIcon,
    startToEndSurfaceColor = actionSurfaceColor,
    endToStartIcon = actionIcon,
    endToStartSurfaceColor = actionSurfaceColor,
    modifier = modifier,
    content = content
)

@Composable
fun SwipeToActionBox(
    state: SwipeToDismissBoxState = rememberSwipeToDismissBoxState(),
    onDismiss: (SwipeToDismissBoxValue) -> Unit,
    startToEndIcon: @Composable () -> Unit,
    startToEndSurfaceColor: Color = MaterialTheme.colorScheme.primary,
    endToStartIcon: @Composable () -> Unit,
    endToStartSurfaceColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val (startButtonWidth, endButtonWidth) = if (state.dismissDirection == SwipeToDismissBoxValue.Settled) 0.dp to 0.dp
    else with(LocalDensity.current) {
        val offset = state.requireOffset()

        val buttonWidth = maxOf(48.dp, state.requireOffset().absoluteValue.toDp() - 4.dp)

        if (offset > 0) buttonWidth to 0.dp
        else 0.dp to buttonWidth
    }

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Surface(
                shape = CircleShape,
                color = startToEndSurfaceColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(startButtonWidth),
                    contentAlignment = Alignment.Center
                ) { startToEndIcon() }
            }

            Spacer(Modifier.weight(1f))

            Surface(
                shape = CircleShape,
                color = endToStartSurfaceColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(endButtonWidth),
                    contentAlignment = Alignment.Center
                ) { endToStartIcon() }
            }
        },
        onDismiss = onDismiss,
        content = content,
        modifier = modifier
    )
}