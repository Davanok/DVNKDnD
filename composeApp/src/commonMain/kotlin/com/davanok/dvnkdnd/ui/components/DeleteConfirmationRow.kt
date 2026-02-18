package com.davanok.dvnkdnd.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.confirm
import dvnkdnd.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteWithConfirmationButton(
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteConfirm by remember { mutableStateOf(false) }
    val color by animateColorAsState(
        targetValue = if (deleteConfirm) MaterialTheme.colorScheme.primaryContainer
        else Color.Transparent
    )
    AnimatedContent(
        modifier = modifier.background(color, CircleShape),
        targetState = deleteConfirm,
        transitionSpec = { slideInHorizontally{ -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut() }
    ) { state ->
        Row {
            if (state) {
                IconButton(onClick = { deleteConfirm = false }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.cancel)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(Res.string.confirm)
                    )
                }
            } else {
                IconButton(onClick = { deleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete)
                    )
                }
            }
        }
    }
}