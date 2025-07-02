package com.davanok.dvnkdnd.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.error
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
    onBack: () -> Unit
) = FullScreenCard(
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
        TextButton(
            onClick = onBack
        ) {
            Text(text = stringResource(Res.string.back))
        }
    }
)
