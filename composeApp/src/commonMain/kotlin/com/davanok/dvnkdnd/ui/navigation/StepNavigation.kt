package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.next
import dvnkdnd.composeapp.generated.resources.previous
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepNavigation(
    label: (@Composable () -> Unit)? = null,
    previous: (() -> Unit)? = null,
    cancel: (() -> Unit)? = null,
    next: (() -> Unit)? = null,
    nextEnabled: Boolean = true,
    previousEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold (
        modifier = modifier,
        topBar = { label?.let { TopAppBar(title = label) } },
        bottomBar = {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                previous?.let {
                    Button(
                        onClick = previous,
                        enabled = previousEnabled
                    ) {
                        Text(stringResource(Res.string.previous))
                    }
                }
                cancel?.let {
                    Button(
                        onClick = cancel,
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
                next?.let {
                    Button(
                        onClick = next,
                        enabled = nextEnabled
                    ) {
                        Text(stringResource(Res.string.next))
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}