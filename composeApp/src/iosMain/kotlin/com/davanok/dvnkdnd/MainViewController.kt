package com.davanok.dvnkdnd

import androidx.compose.ui.window.ComposeUIViewController
import com.davanok.dvnkdnd.di.IOSAppGraph
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController
import platform.UIKit.setStatusBarStyle

fun MainViewController(): UIViewController {
    val graph = createGraph<IOSAppGraph>()
    return ComposeUIViewController {
        graph.app(onThemeChanged = ::onThemeChanged)
    }
}

private fun onThemeChanged(isDarkTheme: Boolean) {
    UIApplication.sharedApplication.setStatusBarStyle(
        if (isDarkTheme) UIStatusBarStyleDarkContent
        else UIStatusBarStyleLightContent
    )
}