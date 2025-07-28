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
import com.davanok.dvnkdnd.data.platform.calculateWindowSizeClass
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.ui.components.ColorScheme
import com.davanok.dvnkdnd.ui.components.LocalColorScheme
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveNavigationInfo
import com.davanok.dvnkdnd.ui.components.adaptive.LocalAdaptiveInfo
import com.davanok.dvnkdnd.ui.components.adaptive.calculateNavSuiteType
import com.davanok.dvnkdnd.ui.navigation.NavigationHost
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
            Surface {
                val adaptiveInfo = AdaptiveNavigationInfo(
                    windowSizeClass = calculateWindowSizeClass(),
                    layoutType = calculateNavSuiteType()
                )
                CompositionLocalProvider(
                    LocalColorScheme provides ColorScheme(darkTheme),
                    LocalAdaptiveInfo provides adaptiveInfo
                ) {
                    NavigationHost(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}