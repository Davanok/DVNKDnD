package com.davanok.dvnkdnd

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.davanok.dvnkdnd.data.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "DVNKDnD",
    ) {
        App()
    }
}