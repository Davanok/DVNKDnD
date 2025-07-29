package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.model.ui.isCompact

@Composable
fun TwoPane(
    modifier: Modifier = Modifier,
    firstPane: @Composable BoxScope.(twoPane: Boolean) -> Unit,
    secondPane: @Composable BoxScope.() -> Unit,
) {
    val info = LocalAdaptiveInfo.current
    val windowSizeClass = info.windowSizeClass

    if (windowSizeClass.isCompact())
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