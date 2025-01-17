package com.davanok.dvnkdnd.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize
import com.davanok.dvnkdnd.data.types.ui.WindowSizeClass

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val size = with(density) { windowInfo.containerSize.toSize().toDpSize() }
    return WindowSizeClass.calculateFromSize(size)
}