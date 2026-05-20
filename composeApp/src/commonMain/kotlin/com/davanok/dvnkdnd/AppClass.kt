package com.davanok.dvnkdnd

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.theme.DVNKDnDAppTheme
import com.davanok.dvnkdnd.ui.theme.LocalLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Inject
class AppClass(
    private val logger: Logger,
    private val metroVmf: MetroViewModelFactory
) {
    @Composable
    operator fun invoke(onThemeChanged: (isDarkTheme: Boolean) -> Unit) =
        AppUi(logger, metroVmf, onThemeChanged)
}

@Composable
private fun AppUi(
    logger: Logger,
    metroVmf: MetroViewModelFactory,
    onThemeChanged: (isDarkTheme: Boolean) -> Unit
) {
    DVNKDnDAppTheme(onThemeChanged = onThemeChanged) {
        CompositionLocalProvider(
            LocalMetroViewModelFactory provides metroVmf,
            LocalLogger provides logger
        ) {
            Surface {
                NavigationHost(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}