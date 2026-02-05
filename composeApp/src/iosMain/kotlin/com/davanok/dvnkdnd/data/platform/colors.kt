package com.davanok.dvnkdnd.data.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun getColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) darkColorScheme() else expressiveLightColorScheme()
