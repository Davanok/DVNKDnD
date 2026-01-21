package com.davanok.dvnkdnd.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import com.davanok.dvnkdnd.data.platform.clipEntryOf
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.copied
import dvnkdnd.composeapp.generated.resources.copy
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.info
import dvnkdnd.composeapp.generated.resources.ok
import dvnkdnd.composeapp.generated.resources.refresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    onRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var exceptionInfo by remember { mutableStateOf<Throwable?>(null) }
    FullScreenCard(
        modifier = modifier,
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
            onRefresh?.let {
                TextButton(
                    onClick = onRefresh
                ) {
                    Text(text = stringResource(Res.string.refresh))
                }
            }
        }
    )
    if (exceptionInfo != null)
        AlertDialog(
            text = {
                SelectionContainer {
                    Text(exceptionInfo.toString())
                }
                   },
            onDismissRequest = { exceptionInfo = null },
            confirmButton = {
                TextButton(onClick = { exceptionInfo = null }) {
                    Text(text = stringResource(Res.string.ok))
                }
            },
            dismissButton = exception?.let {
                {
                    var copied by remember { mutableStateOf(false) }
                    val clipboard = LocalClipboard.current
                    val coroutineScope = rememberCoroutineScope()
                    TextButton(onClick = {
                        val clipEntry = clipEntryOf(exceptionInfo.toString())
                        if (!copied)
                            coroutineScope.launch {
                                clipboard.setClipEntry(clipEntry)
                                copied = true
                                delay(2000)
                                copied = false
                            }
                    }) {
                        AnimatedContent(
                            copied,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220, delayMillis = 90))
                                    .togetherWith(fadeOut(animationSpec = tween(220, delayMillis = 90)))
                            }
                        ) {
                            Text(
                                text = stringResource(if (copied) Res.string.copied else Res.string.copy)
                            )
                        }
                    }
                }
            }
        )
}
