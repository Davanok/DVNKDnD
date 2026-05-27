package com.davanok.dvnkdnd.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.davanok.dvnkdnd.ui.components.ToasterState

val LocalToasterState = staticCompositionLocalOf<ToasterState> {
    error("LocalToasterState not provided")
}