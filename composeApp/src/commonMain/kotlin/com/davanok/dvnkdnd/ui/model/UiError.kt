package com.davanok.dvnkdnd.ui.model

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.ui.components.UiMessage
import kotlin.uuid.Uuid

@Immutable
sealed class UiError(val message: String, val exception: Throwable? = null) {
    class Critical(message: String, exception: Throwable?) : UiError(message, exception)
    class Warning(message: String, exception: Throwable? = null) : UiError(message, exception)
}
fun UiError?.isCritical() = this is UiError.Critical

fun UiError.toUiMessage() = when(this) {
    is UiError.Warning -> UiMessage.Warning(
        id = Uuid.NIL,
        message = message,
        error = exception
    )
    is UiError.Critical -> UiMessage.Error(
        id = Uuid.NIL,
        message = message,
        error = exception
    )
}