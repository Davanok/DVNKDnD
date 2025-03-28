package com.davanok.dvnkdnd

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.annotation.DelicateCoilApi
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.ui.navigation.HostScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@OptIn(DelicateCoilApi::class)
@Composable
fun initApp() {
    Napier.base(DebugAntilog())
}

@Composable
@Preview
fun App() {
    KoinContext {
        initApp()
        MaterialTheme(
            colorScheme = getColorScheme()
        ) {
            HostScreen()
        }
    }
}