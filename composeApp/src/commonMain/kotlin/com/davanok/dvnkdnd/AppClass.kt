package com.davanok.dvnkdnd

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.ui.components.ToasterState
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.theme.DVNKDnDAppTheme
import com.davanok.dvnkdnd.ui.theme.LocalLogger
import com.davanok.dvnkdnd.ui.theme.LocalToasterState
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Inject
class AppClass(
    private val logger: Logger,
    private val metroVmf: MetroViewModelFactory,
    private val toasterState: ToasterState
) {
    @Composable
    operator fun invoke(onThemeChanged: (isDarkTheme: Boolean) -> Unit) =
        AppUi(logger, metroVmf, toasterState, onThemeChanged)
}

@Composable
private fun AppUi(
    logger: Logger,
    metroVmf: MetroViewModelFactory,
    toasterState: ToasterState,
    onThemeChanged: (isDarkTheme: Boolean) -> Unit
) {
    CompositionLocalProvider(
        LocalMetroViewModelFactory provides metroVmf,
        LocalLogger provides logger,
        LocalToasterState provides toasterState
    ) {
        DVNKDnDAppTheme(onThemeChanged = onThemeChanged) {
            NavigationHost(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}