package com.davanok.dvnkdnd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.ui.components.AppColorScheme
import com.davanok.dvnkdnd.ui.components.LocalColorScheme
import com.davanok.dvnkdnd.ui.components.UiToasterSnackbar

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
    val toasterState = LocalToasterState.current

    MaterialExpressiveTheme(
        colorScheme = getColorScheme(isDarkTheme)
    ) {
        CompositionLocalProvider(
            LocalColorScheme provides AppColorScheme(isDarkTheme)
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = toasterState.snackbarHostState,
                        snackbar = { UiToasterSnackbar(snackbarData = it) }
                    )
                }
            ) { paddingValues ->
                Box(Modifier.padding(paddingValues = paddingValues)) {
                    content()
                }
            }
        }
    }
}