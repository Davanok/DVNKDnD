package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterDefaults
import com.dokar.sonner.ToasterState
import com.dokar.sonner.listenMany
import com.dokar.sonner.rememberToasterState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error_info
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.uuid.Uuid


private data class ErrorInfoAction(val exceptionInfo: Throwable)

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
            currentMessages.fastMap(UiMessage::toToast)
        }
    }
    CommonToaster(toaster, modifier)
}
@Composable
fun UiToaster(
    message: UiMessage?,
    onRemoveMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val toaster = rememberToasterState(
        onToastDismissed = { onRemoveMessage() }
    )
    LaunchedEffect(message) {
        if (message != null)
            toaster.show(message.toToast())
    }
    CommonToaster(toaster, modifier)
}
@Composable
private fun CommonToaster(
    state: ToasterState,
    modifier: Modifier
) {
    var exceptionInfo by remember { mutableStateOf<Throwable?>(null) }
    Toaster(
        state = state,
        modifier = modifier,
        darkTheme = LocalColorScheme.current.darkTheme,
        iconSlot = { toast ->
            if (toast.icon == "loading") LoadingIcon()
            else ToasterDefaults.iconSlot(toast)
        },
        actionSlot = { toast ->
            when(val action = toast.action) {
                is ErrorInfoAction -> {
                    IconButton(
                        onClick = { exceptionInfo = action.exceptionInfo }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(Res.string.error_info)
                        )
                    }
                }
                else -> ToasterDefaults.actionSlot(toast)
            }
        }
    )
    if (exceptionInfo != null)
        AlertDialog(
            onDismissRequest = { exceptionInfo = null },
            confirmButton = { exceptionInfo = null }
        )
}
sealed interface UiMessage {
    val id: Uuid

    data class Info(
        val message: String,
        override val id: Uuid = Uuid.random(),
    ) : UiMessage

    data class Success(
        val message: String,
        override val id: Uuid = Uuid.random(),
    ) : UiMessage

    data class Error(
        val message: String,
        override val id: Uuid = Uuid.random(),
        val error: Throwable? = null,
    ) : UiMessage

    data class Warning(
        val message: String,
        override val id: Uuid = Uuid.random(),
        val error: Throwable? = null,
    ) : UiMessage

    data class Loading(
        val message: String,
        override val id: Uuid = Uuid.random(),
    ) : UiMessage
}
private fun UiMessage.toToast(): Toast = when (this) {
    is UiMessage.Info -> Toast(
        id = id,
        message = message,
        type = ToastType.Info,
        duration = Duration.INFINITE
    )

    is UiMessage.Error -> Toast(
        id = id,
        message = message,
        type = ToastType.Error,
        duration = Duration.INFINITE,
        action = error?.let { ErrorInfoAction(it) }
    )
    is UiMessage.Warning -> Toast(
        id = id,
        message = message,
        type = ToastType.Error,
        duration = Duration.INFINITE,
        action = error?.let { ErrorInfoAction(it) }
    )

    is UiMessage.Success -> Toast(
        id = id,
        message = message,
        type = ToastType.Success,
    )

    is UiMessage.Loading -> Toast(
        id = id,
        message = message,
        type = ToastType.Normal,
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