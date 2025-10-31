package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window


@Composable
actual fun MaybeWindow(
    onDismiss: () -> Unit,
    dialogElse: Boolean,
    title: String,
    content: @Composable () -> Unit
) {
    Window(
        onCloseRequest = onDismiss,
        title = title,
        content = {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box { content() }
            }
        }
    )
}