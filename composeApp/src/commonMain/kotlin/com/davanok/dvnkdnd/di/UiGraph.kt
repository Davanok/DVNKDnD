package com.davanok.dvnkdnd.di

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.davanok.dvnkdnd.ui.components.ToasterState
import com.davanok.dvnkdnd.ui.components.UiMessage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface UiGraph {
    @SingleIn(AppScope::class)
    @Provides
    fun provideToasterState(): ToasterState {
        val scope = CoroutineScope(Dispatchers.Main)
        return object : ToasterState {
            override val snackbarHostState: SnackbarHostState = SnackbarHostState()

            override fun showMessage(message: UiMessage) {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(message)
                    if (message.action != null && result == SnackbarResult.ActionPerformed) {
                        message.action.action()
                    }
                }
            }
        }
    }
}