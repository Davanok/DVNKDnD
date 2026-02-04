package com.davanok.dvnkdnd.ui.pages.editCharacter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.model.isCritical
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.no_such_character_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCharacterScreen(
    navigateBack: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    viewModel: EditCharacterViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiToaster(
        messages = uiState.messages,
        onRemoveMessage = viewModel::removeMessage
    )

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            ErrorCard(
                text = it.message,
                exception = it.exception,
                onBack = navigateBack
            )
        }
        else -> uiState.character.let { character ->
            if (character == null)
                ErrorCard(
                    text = stringResource(Res.string.no_such_character_error),
                    onBack = navigateBack
                )
            else
                Content(
                    onBack = navigateBack,
                    navigateToEntityInfo = navigateToEntityInfo,
                    character = character,
                    eventSink = viewModel::eventSink
                )
        }
    }
}

@Composable
private fun Content(
    onBack: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    character: CharacterFull,
    eventSink: (EditCharacterScreenEvent) -> Unit
) {

}