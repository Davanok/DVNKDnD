package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.next
import dvnkdnd.composeapp.generated.resources.previous
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepNavigation(
    label: (@Composable () -> Unit)? = null,
    previous: (() -> Unit)? = null,
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
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                previous?.let {
                    Button(
                        onClick = previous,
                        enabled = previousEnabled
                    ) {
                        Text(text = stringResource(Res.string.previous))
                    }
                }
                next?.let {
                    Button(
                        onClick = next,
                        enabled = nextEnabled
                    ) {
                        Text(text = stringResource(Res.string.next))
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