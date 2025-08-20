package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.new_character_health_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewCharacterHealthScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterHealthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiToaster(
        message = uiState.error?.toUiMessage(),
        onRemoveMessage = viewModel::removeError
    )

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error is UiError.Critical -> uiState.error?.let {
            ErrorCard(
                text = stringResource(it.message),
                exception = it.exception,
                onBack = onBack
            )
        }

        else -> NewEntityStepScaffold (
            modifier = Modifier.fillMaxSize(),
            title = stringResource(Res.string.new_character_health_screen_title),
            additionalContent = {

            },
            onNextClick = { viewModel.commit(onContinue) },
            onBackClick = onBack,
        ) {
            Content(
                // TODO
            )
        }
    }
}

@Composable
private fun Content(
    // TODO
) {

}