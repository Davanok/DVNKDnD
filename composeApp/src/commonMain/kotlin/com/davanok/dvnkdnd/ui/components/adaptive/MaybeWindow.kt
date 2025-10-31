package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.runtime.Composable


@Composable
expect fun MaybeWindow(
    onDismiss: () -> Unit,
    dialogElse: Boolean = false,
    title: String,
    content: @Composable () -> Unit
)