package com.davanok.dvnkdnd.data.platform

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator
import com.davanok.dvnkdnd.data.model.ui.WindowSizeClass

@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    LocalConfiguration.current
    val density = LocalDensity.current
    val metrics = WindowMetricsCalculator
        .getOrCreate()
        .computeCurrentWindowMetrics(LocalActivity.current!!)
    val size = with(density) { metrics.bounds.toComposeRect().size.toDpSize() }
    return WindowSizeClass.calculateFromSize(size)}