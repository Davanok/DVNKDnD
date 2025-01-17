package com.davanok.dvnkdnd.data.platform

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.types.ui.WindowSizeClass

@Composable
expect fun calculateWindowSizeClass(): WindowSizeClass