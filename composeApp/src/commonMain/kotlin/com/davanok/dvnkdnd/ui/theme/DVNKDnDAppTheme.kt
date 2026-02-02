package com.davanok.dvnkdnd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.ui.components.AppColorScheme
import com.davanok.dvnkdnd.ui.components.LocalColorScheme

@Composable
fun DVNKDnDAppTheme(
    onThemeChanged: (isDarkTheme: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(isDarkTheme) {
        onThemeChanged(isDarkTheme)
    }

    MaterialTheme(
        colorScheme = getColorScheme(isDarkTheme)
    ) {
        CompositionLocalProvider(
            LocalColorScheme provides AppColorScheme(isDarkTheme),
            content = content
        )
    }
}