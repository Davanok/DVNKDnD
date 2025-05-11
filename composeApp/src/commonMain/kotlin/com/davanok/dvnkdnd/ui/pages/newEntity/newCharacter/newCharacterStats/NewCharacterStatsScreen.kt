package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.navigation.StepNavigation
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.loading_characters_error
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun NewCharacterStatsScreen(
    characterId: Uuid,
    onBack: (characterId: Uuid) -> Unit,
    onContinue: (characterId: Uuid) -> Unit,
    viewModel: NewCharacterStatsViewModel = koinViewModel(),
) {
    LaunchedEffect(characterId) {
        viewModel.loadCharacterWithModifiers(characterId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.isLoading -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        uiState.character == null -> FullScreenCard {
            val text = stringResource(Res.string.loading_characters_error)
            Icon(
                painter = painterResource(Res.drawable.error),
                contentDescription = text
            )
            Spacer(Modifier.height(16.dp))
            Text(text)
        }
        else -> {
            StepNavigation (
                modifier = Modifier.fillMaxSize(),
                next = { viewModel.createCharacter(onContinue) },
                previous = { onBack(characterId) }
            ) {
                Content(
                    selectedCreationOption = uiState.selectedCreationOptions,
                    onOptionSelected = viewModel::selectCreationOption,
                    character = uiState.character!!,
                    modifiers = uiState.modifiers,
                    onModifiersChange = viewModel::setModifiers
                )
            }
        }
    }
}

@Composable
private fun Content(
    selectedCreationOption: StatsCreationOptions,
    onOptionSelected: (StatsCreationOptions) -> Unit,
    character: CharacterWithModifiers,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CreationOptionsSelector(selectedCreationOption, onOptionSelected)

        ModifiersSelector(
            selectedCreationOption = selectedCreationOption,
            character = character,
            modifiers = modifiers,
            onModifiersChange = onModifiersChange,
            onSelectModifiers = {  }
        )
    }
}
@Composable
private fun CreationOptionsSelector(
    selectedCreationOption: StatsCreationOptions,
    onOptionSelected: (StatsCreationOptions) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        StatsCreationOptions.entries.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selectedCreationOption == option,
                onClick = { onOptionSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(
                    index, StatsCreationOptions.entries.size
                ),
                label = { Text(text = stringResource(option.title)) }
            )
        }
    }
}
