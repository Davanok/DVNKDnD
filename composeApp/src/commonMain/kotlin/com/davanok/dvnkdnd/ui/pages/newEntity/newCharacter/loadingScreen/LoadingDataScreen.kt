package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen

import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.error
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoadingDataScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: LoadingDataViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.checkRequiredEntities(onContinue)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoadingDataCard(
        state = uiState,
        onCancel = onBack
    )
}

@Composable
private fun LoadingDataCard(
    state: LoadingDataUiState,
    onCancel: () -> Unit
) {
    FullScreenCard(
        heroIcon = if (state == LoadingDataUiState.ERROR) {
            {
                Icon(
                    painter = painterResource(Res.drawable.error),
                    contentDescription = stringResource(state.text)
                )
            }
        } else null,
        content = {
            Text(text = stringResource(state.text))
        },
        supportContent =
            if (state != LoadingDataUiState.ERROR) {
                {
                    val stateValues = LoadingDataUiState.entries
                    LinearProgressIndicator(
                        progress = { stateValues.indexOf(state) / stateValues.size.toFloat() }
                    )
                }
            } else null,
        navButtons = {
            TextButton(
                onClick = onCancel
            ) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}



