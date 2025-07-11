package com.davanok.dvnkdnd

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
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

@Suppress("ComposableNaming")
@Composable
fun initApp() {
    Napier.base(DebugAntilog())
}

@Composable
@Preview
fun App() {
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