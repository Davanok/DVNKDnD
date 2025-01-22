package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass
import com.davanok.dvnkdnd.data.types.ui.WindowHeightSizeClass
import com.davanok.dvnkdnd.data.types.ui.WindowSizeClass
import com.davanok.dvnkdnd.data.types.ui.WindowWidthSizeClass

@Composable
fun calculateNavSuiteType(
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
): NavigationSuiteType {
    return with(windowSizeClass) {
        when {
            heightSizeClass == WindowHeightSizeClass.Compact || widthSizeClass == WindowWidthSizeClass.Compact -> NavigationSuiteType.NavigationBar
//            widthSizeClass == WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Large -> NavigationSuiteType.NavigationDrawer
//            else -> NavigationSuiteType.NavigationBar
            else -> NavigationSuiteType.NavigationRail
        }
    }
}