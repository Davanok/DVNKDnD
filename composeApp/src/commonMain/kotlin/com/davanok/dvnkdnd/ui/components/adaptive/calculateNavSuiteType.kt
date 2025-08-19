package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun calculateNavSuiteType(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
): NavigationSuiteType {
    return windowSizeClass.run {
        when {
            windowWidthSizeClass == WindowWidthSizeClass.COMPACT -> NavigationSuiteType.NavigationBar
//            widthSizeClass == WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
//            else -> NavigationSuiteType.NavigationBar
            else -> NavigationSuiteType.NavigationRail
        }
    }
}