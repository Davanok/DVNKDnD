package com.davanok.dvnkdnd.data.platform

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun getColorScheme(darkTheme: Boolean): ColorScheme