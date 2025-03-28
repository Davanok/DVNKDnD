package com.davanok.dvnkdnd.data.model.util

import org.jetbrains.compose.resources.StringResource


sealed class Async<out T> {
    data object Loading : Async<Nothing>()

    data class Error(val errorMessage: StringResource) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}