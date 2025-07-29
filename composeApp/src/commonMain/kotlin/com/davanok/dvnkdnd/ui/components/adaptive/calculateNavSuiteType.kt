package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.model.ui.WindowSizeClass
import com.davanok.dvnkdnd.data.model.ui.isCompact
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass

@Composable
fun calculateNavSuiteType(
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
): NavigationSuiteType {
    return windowSizeClass.run {
        when {
            isCompact() -> NavigationSuiteType.NavigationBar
//            widthSizeClass == WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
//            widthSizeClass == WindowWidthSizeClass.Large -> NavigationSuiteType.NavigationDrawer
//            else -> NavigationSuiteType.NavigationBar
            else -> NavigationSuiteType.NavigationRail
        }
    }
}