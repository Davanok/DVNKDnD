package com.davanok.dvnkdnd

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

fun MainViewController() = ComposeUIViewController {
    App(onThemeChanged = ::onThemeChanged)
}

private fun onThemeChanged(isDarkTheme: Boolean) {
    UIApplication.sharedApplication.setStatusBarStyle(
        if (isDarkTheme) UIStatusBarStyleDarkContent
        else UIStatusBarStyleLightContent
    )
}