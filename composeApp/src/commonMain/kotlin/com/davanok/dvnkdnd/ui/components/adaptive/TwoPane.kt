package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass

@Composable
fun TwoPane(
    modifier: Modifier = Modifier,
    firstPane: @Composable BoxScope.(twoPane: Boolean) -> Unit,
    secondPane: @Composable BoxScope.() -> Unit,
) {
    val windowSizeClass = calculateWindowSizeClass()

    if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact)
        Box(modifier = modifier) {
            firstPane(false)
        }
    else
        Row(modifier = modifier) {
            Box(modifier = Modifier.weight(.5f)) { firstPane(true) }
            Spacer(modifier = Modifier.width(24.dp))
            Box(modifier = Modifier.weight(.5f)) { secondPane() }
        }
}