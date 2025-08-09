package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen

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
                val stateValues = LoadingDataUiState.entries
                CircularProgressIndicator(
                    progress = { stateValues.indexOf(uiState) / stateValues.size.toFloat() }
                )
            },
            content = {
                Text(text = stringResource(uiState.stringRes))
            },
            navButtons = {
                TextButton(onClick = onBack) {
                    Text(text = stringResource(Res.string.cancel))
                }
            }
        )
}



