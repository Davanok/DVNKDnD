package com.davanok.dvnkdnd

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.data.di.commonModule
import com.davanok.dvnkdnd.data.di.databaseModule
import com.davanok.dvnkdnd.data.di.platformModule
import com.davanok.dvnkdnd.data.di.viewModelsModule
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import com.davanok.dvnkdnd.ui.components.AppColorScheme
import com.davanok.dvnkdnd.ui.components.LocalColorScheme
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
import com.davanok.dvnkdnd.ui.providers.LocalMeasurementSystem
import com.davanok.dvnkdnd.ui.providers.MeasurementSystemConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
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
@Preview
fun App() {
    KoinMultiplatformApplication(
        config = koinConfiguration {
            modules(
                platformModule(), commonModule(), databaseModule(), viewModelsModule()
            )
        }
    ) {
        initApp()
        val darkTheme = isSystemInDarkTheme()
        MaterialTheme(
            colorScheme = getColorScheme(darkTheme)
        ) {
            CompositionLocalProvider(
                LocalColorScheme provides AppColorScheme(darkTheme),
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