package com.davanok.dvnkdnd

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.providers.LocalMeasurementSystem
import com.davanok.dvnkdnd.ui.providers.MeasurementSystemConfig
import com.davanok.dvnkdnd.ui.theme.DVNKDnDAppTheme
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Inject
class AppClass(private val metroVmf: MetroViewModelFactory) {
    init {
        Napier.base(DebugAntilog())
    }

    @Composable
    operator fun invoke(onThemeChanged: (isDarkTheme: Boolean) -> Unit) =
        AppUi(metroVmf, onThemeChanged)
}

@Composable
private fun AppUi(
    metroVmf: MetroViewModelFactory,
    onThemeChanged: (isDarkTheme: Boolean) -> Unit
) {
    DVNKDnDAppTheme(onThemeChanged = onThemeChanged) {
        CompositionLocalProvider(
            LocalMeasurementSystem provides MeasurementSystemConfig(MeasurementSystem.METRIC, MeasurementSystem.METRIC),
            LocalMetroViewModelFactory provides metroVmf
        ) {
            Surface {
                NavigationHost(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}