package com.davanok.dvnkdnd.data.model.ui

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.ui.components.UiMessage
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class UiError(val message: StringResource, val details: Throwable? = null) {
    class Critical(message: StringResource, details: Throwable) : UiError(message, details)
    class Warning(message: StringResource, details: Throwable? = null) : UiError(message, details)
}

@Composable
fun UiError.toUiMessage() = when(this) {
    is UiError.Warning -> UiMessage.Warning(
        message = stringResource(message),
        error = details
    )
    is UiError.Critical -> UiMessage.Error(
        message = stringResource(message),
        error = details
    )
}