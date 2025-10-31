package com.davanok.dvnkdnd.ui.components

import androidx.compose.runtime.staticCompositionLocalOf

data class AppColorScheme(
    val darkTheme: Boolean
)
val LocalColorScheme = staticCompositionLocalOf<AppColorScheme> {
    error("CompositionLocal ColorScheme not provided")
}