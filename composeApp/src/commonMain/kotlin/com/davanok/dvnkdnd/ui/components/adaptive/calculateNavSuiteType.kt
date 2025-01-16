package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass

@Composable
fun calculateNavSuiteType(
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
): NavigationSuiteType {
    return with(windowSizeClass) {
        when {
            heightSizeClass == WindowHeightSizeClass.Compact -> NavigationSuiteType.NavigationBar
            widthSizeClass == WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
            widthSizeClass == WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteType.NavigationBar
        }
    }
}