package com.davanok.dvnkdnd.ui.components

import androidx.compose.runtime.staticCompositionLocalOf

data class ColorScheme(
    val darkTheme: Boolean
)
val LocalColorScheme = staticCompositionLocalOf<ColorScheme> {
    error("CompositionLocal ContentType not provided")
}