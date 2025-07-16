package com.davanok.dvnkdnd.data.model.ui

import org.jetbrains.compose.resources.StringResource

sealed class UiError(val message: StringResource, val details: Throwable? = null) {
    class Critical(message: StringResource, details: Throwable) : UiError(message, details)
    class Warning(message: StringResource, details: Throwable? = null) : UiError(message, details)
}