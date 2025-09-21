package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.continue_str
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoadingDataScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: LoadingDataViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is LoadingDataUiState.Finish) onContinue()
    }

    if (uiState is LoadingDataUiState.Error)
        ErrorCard(
            text = stringResource(uiState.stringRes),
            exception = (uiState as LoadingDataUiState.Error).exception,
            onBack = onBack,
            onRefresh = viewModel::checkRequiredEntities
        )
    else
        FullScreenCard(
            heroIcon = {
                if (uiState == LoadingDataUiState.NoInternet) Image(
                    imageVector = Icons.Default.SignalWifiConnectedNoInternet4,
                    contentDescription = stringResource(uiState.stringRes)
                )
                else {
                    val stateValues = LoadingDataUiState.entries
                    CircularProgressIndicator(
                        progress = { stateValues.indexOf(uiState) / stateValues.size.toFloat() }
                    )
                }
            },
            content = {
                Text(text = stringResource(uiState.stringRes))
            },
            navButtons = {
                if (uiState == LoadingDataUiState.NoInternet)
                    TextButton(onClick = onContinue) {
                        Text(text = stringResource(Res.string.continue_str))
                    }
                TextButton(onClick = onBack) {
                    Text(text = stringResource(Res.string.cancel))
                }
            }
        )
}



