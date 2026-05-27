package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.dismiss_snackbar
import org.jetbrains.compose.resources.stringResource

interface ToasterState {
    val snackbarHostState: SnackbarHostState
    fun showMessage(message: UiMessage)

    fun showInfo(message: String, action: MessageAction? = null) =
        showMessage(UiMessage.Info(message, action = action))

    fun showSuccess(message: String, action: MessageAction? = null) =
        showMessage(UiMessage.Success(message, action = action))

    fun showWarning(message: String, error: Throwable? = null, action: MessageAction? = null) =
        showMessage(UiMessage.Warning(message, error, action = action))

    fun showError(message: String, error: Throwable? = null, action: MessageAction? = null) =
        showMessage(UiMessage.Error(message, error, action = action))

    fun showLoading(message: String, action: MessageAction? = null) =
        showMessage(UiMessage.Loading(message, action = action))
}

data class MessageAction(
    val label: String,
    val action: () -> Unit
)

sealed class UiMessage(
    override val message: String,
    val action: MessageAction? = null,
    override val withDismissAction: Boolean = true,
    override val duration: SnackbarDuration = if (action == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
) : SnackbarVisuals {
    override val actionLabel: String?
        get() = action?.label

    class Info(
        message: String,
        action: MessageAction? = null,
        withDismissAction: Boolean = true
    ) : UiMessage(message, action, withDismissAction)

    class Success(
        message: String,
        action: MessageAction? = null,
        withDismissAction: Boolean = true
    ) : UiMessage(message, action, withDismissAction)

    class Warning(
        message: String,
        val error: Throwable? = null,
        action: MessageAction? = null,
        withDismissAction: Boolean = true
    ) : UiMessage(message, action, withDismissAction)

    class Error(
        message: String,
        val error: Throwable? = null,
        action: MessageAction? = null,
        withDismissAction: Boolean = true
    ) : UiMessage(message, action, withDismissAction)

    class Loading(
        message: String,
        action: MessageAction? = null,
        withDismissAction: Boolean = true
    ) : UiMessage(message, action, withDismissAction)
}

@Composable
fun UiToasterSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
    if (snackbarData.visuals is UiMessage) {
        UiMessageSnackbar(
            snackbarData = snackbarData,
            modifier = modifier,
            actionOnNewLine = actionOnNewLine,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            actionColor = actionColor,
            actionContentColor = actionContentColor,
            dismissActionContentColor = dismissActionContentColor
        )
    } else {
        Snackbar(
            snackbarData = snackbarData,
            modifier = modifier,
            actionOnNewLine = actionOnNewLine,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            actionColor = actionColor,
            actionContentColor = actionContentColor,
            dismissActionContentColor = dismissActionContentColor
        )
    }
}

@Composable
private fun UiMessageSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? =
        actionLabel?.let {
            {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = actionColor),
                    onClick = { snackbarData.performAction() },
                    content = { Text(actionLabel) },
                )
            }
        }

    val dismissActionComposable: (@Composable () -> Unit)? =
        if (snackbarData.visuals.withDismissAction) {
            {
                DescriptionIconButton(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(Res.string.dismiss_snackbar),
                    onClick = { snackbarData.dismiss() }
                )
            }
        } else null

    Snackbar(
        modifier = modifier,
        action = actionComposable,
        dismissAction = dismissActionComposable,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
        content = {
            UiMessageSnackbarContent(
                message = snackbarData.visuals as UiMessage
            )
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UiMessageSnackbarContent(
    message: UiMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (message) {
            is UiMessage.Error -> Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null
            )
            is UiMessage.Info -> Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null
            )
            is UiMessage.Loading -> LoadingIndicator(Modifier.size(24.dp))
            is UiMessage.Success -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            is UiMessage.Warning -> Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(text = message.message)
    }
}