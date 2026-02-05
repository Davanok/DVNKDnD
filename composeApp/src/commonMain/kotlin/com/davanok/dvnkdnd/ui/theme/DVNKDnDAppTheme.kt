package com.davanok.dvnkdnd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.ui.components.AppColorScheme
import com.davanok.dvnkdnd.ui.components.LocalColorScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DVNKDnDAppTheme(
    onThemeChanged: (isDarkTheme: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(isDarkTheme) {
        onThemeChanged(isDarkTheme)
    }

    MaterialExpressiveTheme(
        colorScheme = getColorScheme(isDarkTheme)
    ) {
        CompositionLocalProvider(
            LocalColorScheme provides AppColorScheme(isDarkTheme),
            content = content
        )
    }
}