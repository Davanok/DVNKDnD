package com.davanok.dvnkdnd.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.info
import dvnkdnd.composeapp.generated.resources.ok
import dvnkdnd.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadingCard(
    progress: (() -> Float)? = null,
    support: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) = FullScreenCard(
    modifier = modifier,
    content = {
        if (progress == null) CircularProgressIndicator()
        else CircularProgressIndicator(progress=progress)
    },
    supportContent = support
)

@Composable
fun ErrorCard(
    text: String,
    exception: Throwable? = null,
    onBack: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null
) {
    var exceptionInfo by remember { mutableStateOf<Throwable?>(null) }
    FullScreenCard(
        heroIcon = {
            Icon(
                painter = painterResource(Res.drawable.error),
                contentDescription = text
            )
        },
        content = {
            Text(text)
        },
        navButtons = {
            exception?.let {
                TextButton(
                    onClick = { exceptionInfo = exception }
                ) {
                    Text(text = stringResource(Res.string.info))
                }
            }
            onBack?.let {
                TextButton(
                    onClick = onBack
                ) {
                    Text(text = stringResource(Res.string.back))
                }
            }
            refresh?.let {
                TextButton(
                    onClick = refresh
                ) {
                    Text(text = stringResource(Res.string.refresh))
                }
            }
        }
    )
    if (exceptionInfo != null)
        AlertDialog(
            text = { Text(exceptionInfo.toString()) },
            onDismissRequest = { exceptionInfo = null },
            confirmButton = {
                TextButton(onClick = { exceptionInfo = null }) {
                    Text(text = stringResource(Res.string.ok))
                }
            }
        )
}
