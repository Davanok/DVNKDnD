package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
actual fun MaybeWindow(
    onDismiss: () -> Unit,
    dialogElse: Boolean,
    title: String,
    content: @Composable () -> Unit
) {
    if (dialogElse) AdaptiveModalSheet(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        content = content
    )
}