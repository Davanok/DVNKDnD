package com.davanok.dvnkdnd

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.davanok.dvnkdnd.data.platform.getColorScheme
import com.davanok.dvnkdnd.ui.components.image.PlatformImageFetcher
import com.davanok.dvnkdnd.ui.components.image.PlatformImageKeyer
import com.davanok.dvnkdnd.ui.navigation.host.HostScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    Napier.base(DebugAntilog())
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(PlatformImageKeyer())
                add(PlatformImageFetcher.Factory())
            }.build()
    }
    KoinContext {
        MaterialTheme(
            colorScheme = getColorScheme()
        ) {
            HostScreen()
        }
    }
}