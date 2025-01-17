package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass
import com.davanok.dvnkdnd.data.types.ui.WindowWidthSizeClass

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    secondPane: @Composable () -> Unit,
    firstPane: @Composable (twoPane: Boolean) -> Unit,
) {
    val windowSizeClass = calculateWindowSizeClass()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact ->
            Box(
                modifier = modifier.padding(horizontal = 16.dp)
            ) {
                firstPane(false)
            }

        else -> {
            Row(
                modifier = modifier.padding(horizontal = 24.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) { firstPane(true) }
                Spacer(modifier = Modifier.width(24.dp))
                Box(modifier = Modifier.weight(1f)) { secondPane() }
            }
        }
    }
}