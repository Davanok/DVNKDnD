package com.davanok.dvnkdnd

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.data.di.commonModule
import com.davanok.dvnkdnd.data.di.databaseModule
import com.davanok.dvnkdnd.data.di.platformModule
import com.davanok.dvnkdnd.data.di.viewModelsModule
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.providers.LocalMeasurementSystem
import com.davanok.dvnkdnd.ui.providers.MeasurementSystemConfig
import com.davanok.dvnkdnd.ui.theme.DVNKDnDAppTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@Suppress("ComposableNaming")
@Composable
fun initApp() {
    Napier.base(DebugAntilog())
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(onThemeChanged: (isDarkTheme: Boolean) -> Unit) {
    KoinMultiplatformApplication(
        config = koinConfiguration {
            modules(
                platformModule(), commonModule(), databaseModule(), viewModelsModule()
            )
        }
    ) {
        initApp()
        DVNKDnDAppTheme(
            onThemeChanged = onThemeChanged
        ) {
            CompositionLocalProvider(
                LocalMeasurementSystem provides MeasurementSystemConfig(MeasurementSystem.METRIC, MeasurementSystem.METRIC)
            ) {
                Surface {
                    NavigationHost(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}