package com.davanok.dvnkdnd.data.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent

@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    if(enabled)
        Box(
            modifier = Modifier.onKeyEvent {
                if(it.key == Key.Escape) onBack()
                true
            }
        )
}