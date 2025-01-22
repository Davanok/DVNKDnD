package com.davanok.dvnkdnd

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.davanok.dvnkdnd.data.di.initKoin
import java.awt.Dimension

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "DVNKDnD"
    ) {
        with (LocalDensity.current) {
            window.minimumSize = Dimension(240.dp.roundToPx(), 426.dp.roundToPx())
        }
        App()
    }
}