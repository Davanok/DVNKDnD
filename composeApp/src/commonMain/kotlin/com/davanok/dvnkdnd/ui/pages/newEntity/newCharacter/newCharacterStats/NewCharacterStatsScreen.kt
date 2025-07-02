package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.navigation.StepNavigation
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers_selectors
import dvnkdnd.composeapp.generated.resources.modifiers_selectors_hint
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
    if (uiState.isLoading) LoadingCard()
    else
        StepNavigation (
            modifier = Modifier.fillMaxSize(),
            next = { viewModel.createCharacter(onContinue) },
            previous = { onBack(characterId) }
        ) {
            Content(
                selectedCreationOption = uiState.selectedCreationOptions,
                onOptionSelected = viewModel::selectCreationOption,
                allEntitiesWithModifiers = uiState.allEntitiesWithModifiers,
                selectedModifiersBonuses = uiState.selectedModifiersBonuses,
                modifiers = uiState.modifiers,
                onModifiersChange = viewModel::setModifiers,
                onSelectModifier = viewModel::selectModifier
            )
        }
}

@Composable
private fun Content(
    selectedCreationOption: StatsCreationOptions,
    onOptionSelected: (StatsCreationOptions) -> Unit,
    allEntitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
    onSelectModifier: (DnDModifier) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CreationOptionsSelector(selectedCreationOption, onOptionSelected)

        ModifiersSelector(
            selectedCreationOption = selectedCreationOption,
            allEntitiesWithModifiers = allEntitiesWithModifiers,
            selectedModifiersBonuses = selectedModifiersBonuses,
            modifiers = modifiers,
            onModifiersChange = onModifiersChange,
            onSelectModifiers = onSelectModifier
        )
    }
}
@Composable
private fun CreationOptionsSelector(
    selectedCreationOption: StatsCreationOptions,
    onOptionSelected: (StatsCreationOptions) -> Unit,
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    Row {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.weight(1f)
        ) {
            StatsCreationOptions.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = selectedCreationOption == option,
                    onClick = { onOptionSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = StatsCreationOptions.entries.size
                    ),
                    label = {
                        Text(
                            text = stringResource(option.title),
                            maxLines = 1
                        )
                    }
                )
            }
        }
        IconButton(
            onClick = { showInfoDialog = !showInfoDialog }
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(Res.string.about_modifiers_selectors)
            )
        }
    }
    if (showInfoDialog)
        AboutModifiersSelectorsDialog(
            onDismiss = { showInfoDialog = false }
        )
}
@Composable
private fun AboutModifiersSelectorsDialog(
    onDismiss: () -> Unit
) {
    val html = stringResource(Res.string.modifiers_selectors_hint)
    AdaptiveModalSheet(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.about_modifiers_selectors)) }
    ) {
        Text(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            text = remember(Unit) { htmlToAnnotatedString(html) }
        )
    }
}
