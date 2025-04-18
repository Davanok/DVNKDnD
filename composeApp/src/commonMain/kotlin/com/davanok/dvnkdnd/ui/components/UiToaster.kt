package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterDefaults
import com.dokar.sonner.listenMany
import com.dokar.sonner.rememberToasterState
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Composable
fun UiToaster(
    messages: List<UiMessage>,
    onRemoveMessage: (id: Uuid) -> Unit,
    modifier: Modifier = Modifier,
) {
    val toaster = rememberToasterState(
        onToastDismissed = { onRemoveMessage(it.id as Uuid) },
    )
    val currentMessages by rememberUpdatedState(messages)

    LaunchedEffect(toaster) {
        // Listen to State<List<UiMessage>> changes and map to toasts
        toaster.listenMany {
            currentMessages.map { message ->
                message.toToast(onDismiss = { onRemoveMessage(message.id) })
            }
        }
    }

    Toaster(
        state = toaster,
        modifier = modifier,
        richColors = true,
        darkTheme = LocalColorScheme.current.darkTheme,
        iconSlot = { toast ->
            if (toast.icon == "loading") LoadingIcon()
            else ToasterDefaults.iconSlot(toast)
        }
    )
}
sealed interface UiMessage {
    val id: Uuid

    data class Success(
        val message: String,
        override val id: Uuid = Uuid.random(),
    ) : UiMessage

    data class Error(
        val message: String,
        override val id: Uuid = Uuid.random(),
        val error: Throwable? = null,
    ) : UiMessage

    data class Loading(
        val message: String,
        override val id: Uuid = Uuid.random(),
    ) : UiMessage
}
private fun UiMessage.toToast(onDismiss: (toast: Toast) -> Unit): Toast = when (this) {
    is UiMessage.Error -> Toast(
        id = id,
        message = message,
        type = ToastType.Error,
        duration = Duration.INFINITE,
        action = TextToastAction(text = "Dismiss", onClick = onDismiss),
    )

    is UiMessage.Success -> Toast(
        id = id,
        message = message,
        type = ToastType.Success,
        action = TextToastAction(text = "Dismiss", onClick = onDismiss),
    )

    is UiMessage.Loading -> Toast(
        id = id,
        message = message,
        type = ToastType.Normal,
        action = TextToastAction(text = "Dismiss", onClick = onDismiss),
        icon = "loading"
    )
}

@Composable
private fun LoadingIcon(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(end = 16.dp)) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp)
        )
    }
}