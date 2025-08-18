package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass

@Composable
fun calculateNavSuiteType(
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
): NavigationSuiteType {
    return windowSizeClass.run {
        when {
            widthSizeClass <= WindowWidthSizeClass.Compact -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Large -> NavigationSuiteType.NavigationDrawer
//            else -> NavigationSuiteType.NavigationBar
            else -> NavigationSuiteType.NavigationBar
        }
    }
}