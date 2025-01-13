package com.davanok.dvnkdnd

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.ui.navigation.host.HostScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    Napier.base(DebugAntilog())
    KoinContext {
        MaterialTheme {
            HostScreen()
        }
    }
}