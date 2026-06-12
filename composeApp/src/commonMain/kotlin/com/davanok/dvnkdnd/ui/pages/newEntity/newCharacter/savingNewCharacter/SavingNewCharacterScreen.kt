package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.UiStateHandler
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back_to_characters_list
import dvnkdnd.composeapp.generated.resources.go_to_character
import dvnkdnd.composeapp.generated.resources.successfully_created_character
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun SavingNewCharacterScreen(
    onBack: () -> Unit,
    onGoToCharacter: (characterId: Uuid) -> Unit,
    viewModel: SavingNewCharacterViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiStateHandler(
        isLoading = uiState.isLoading,
        error = uiState.error
    ) {
        FullScreenCard(
            heroIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(Res.string.successfully_created_character)
                )
            },
            navButtons = {
                TextButton(
                    onClick = onBack
                ) {
                    Text(text = stringResource(Res.string.back_to_characters_list))
                }
                TextButton(
                    onClick = { onGoToCharacter(uiState.characterId!!) }
                ) {
                    Text(text = stringResource(Res.string.go_to_character))
                }
            }
        ) {
            Text(text = stringResource(Res.string.successfully_created_character))
        }
    }
}