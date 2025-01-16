package com.davanok.dvnkdnd.data.platform

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getColorScheme(): ColorScheme {
    val darkTheme = isSystemInDarkTheme()
    return if (darkTheme) darkColorScheme() else lightColorScheme()
}
