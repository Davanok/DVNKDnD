package com.davanok.dvnkdnd.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.platform.clipEntryOf
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.ui.model.isCritical
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
fun UiStateHandler(
    isLoading: Boolean,
    error: UiError?,
    errorOnBack: (() -> Unit)? = null,
    errorOnRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when {
        isLoading -> LoadingCard(modifier = modifier)
        error.isCritical() -> error?.let {
            ErrorCard(
                text = error.message,
                exception = error.exception,
                onBack = errorOnBack,
                onRefresh = errorOnRefresh,
                modifier = modifier
            )
        }
        else -> Box(modifier) { content() }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingCard(
    progress: (() -> Float)? = null,
    support: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) = Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (progress == null) CircularWavyProgressIndicator()
        else CircularWavyProgressIndicator(progress=progress)

        support?.invoke()
    }
}

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
