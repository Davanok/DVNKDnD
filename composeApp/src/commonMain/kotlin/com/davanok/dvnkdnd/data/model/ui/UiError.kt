package com.davanok.dvnkdnd.data.model.ui

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.ui.components.UiMessage
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

sealed class UiError(val message: StringResource, val exception: Throwable? = null) {
    class Critical(message: StringResource, exception: Throwable?) : UiError(message, exception)
    class Warning(message: StringResource, exception: Throwable? = null) : UiError(message, exception)
}
fun UiError?.isCritical() = this is UiError.Critical

@Composable
fun UiError.toUiMessage() = when(this) {
    is UiError.Warning -> UiMessage.Warning(
        id = Uuid.NIL,
        message = stringResource(message),
        error = exception
    )
    is UiError.Critical -> UiMessage.Error(
        id = Uuid.NIL,
        message = stringResource(message),
        error = exception
    )
}