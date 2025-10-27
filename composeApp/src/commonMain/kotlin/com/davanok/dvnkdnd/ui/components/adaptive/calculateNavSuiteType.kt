package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
fun calculateNavSuiteType(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
): NavigationSuiteType {
    return windowSizeClass.run {
        when {
            isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> NavigationSuiteType.NavigationRail

            else -> NavigationSuiteType.NavigationBar
        }
    }
}