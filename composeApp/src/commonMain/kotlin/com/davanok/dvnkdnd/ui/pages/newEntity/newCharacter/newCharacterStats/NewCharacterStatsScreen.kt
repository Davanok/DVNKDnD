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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.davanok.dvnkdnd.data.model.entities.character.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.append
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import com.davanok.dvnkdnd.ui.components.newEntity.newCharacter.NewCharacterTopBarAdditionalContent
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers_selectors
import dvnkdnd.composeapp.generated.resources.modifiers_selectors_hint
import dvnkdnd.composeapp.generated.resources.new_character_stats_screen_title
import dvnkdnd.composeapp.generated.resources.no_modifiers_for_info
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun NewCharacterStatsScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterStatsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiToaster(
        message = uiState.error?.toUiMessage(),
        onRemoveMessage = viewModel::removeWarning
    )

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            ErrorCard(
                text = it.message,
                exception = it.exception,
                onBack = onBack
            )
        }
        else -> NewEntityStepScaffold (
            modifier = Modifier.fillMaxSize(),
            title = stringResource(Res.string.new_character_stats_screen_title),
            additionalContent = {
                NewCharacterTopBarAdditionalContent(uiState.character)
            },
            onNextClick = { viewModel.commit(onContinue) },
            onBackClick = onBack,
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
}

@Composable
private fun Content(
    selectedCreationOption: StatsCreationOptions,
    onOptionSelected: (StatsCreationOptions) -> Unit,
    allEntitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
    onSelectModifier: (DnDModifierBonus) -> Unit
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CreationOptionsSelector(
            selectedCreationOption = selectedCreationOption,
            onOptionSelected = onOptionSelected,
            onInfoClick = { showInfoDialog = !showInfoDialog }
        )

        ModifiersSelector(
            selectedCreationOption = selectedCreationOption,
            allEntitiesWithModifiers = allEntitiesWithModifiers,
            selectedModifiersBonuses = selectedModifiersBonuses,
            modifiers = modifiers,
            onModifiersChange = onModifiersChange,
            onSelectModifiers = onSelectModifier
        )
    }
    if (showInfoDialog)
        AboutModifiersSelectorsDialog(
            allEntitiesWithModifiers = allEntitiesWithModifiers,
            selectedModifiersBonuses = selectedModifiersBonuses,
            onDismiss = { showInfoDialog = false }
        )
}
@Composable
private fun CreationOptionsSelector(
    selectedCreationOption: StatsCreationOptions,
    onOptionSelected: (StatsCreationOptions) -> Unit,
    onInfoClick: () -> Unit
) {
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
            onClick = onInfoClick
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(Res.string.about_modifiers_selectors)
            )
        }
    }
}
@Composable
private fun AboutModifiersSelectorsDialog(
    allEntitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    onDismiss: () -> Unit
) {
    val html = stringResource(Res.string.modifiers_selectors_hint)
    AdaptiveModalSheet(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.about_modifiers_selectors)) }
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = remember(Unit) { htmlToAnnotatedString(html) }
            )

            Text(
                text = buildAnnotatedString {
                    allEntitiesWithModifiers.fastForEach { entity ->
                        if (entity.modifiers.isEmpty()) return@fastForEach
                        withStyle(MaterialTheme.typography.labelLarge.toSpanStyle()) {
                            append(entity.entity.type.stringRes)
                            append(' ')
                            append(entity.entity.name)
                        }
                        entity.modifiers.groupBy { it.stat }.forEach { (stat, modifiers) ->
                            append("\n\t")
                            append(stat.stringRes)
                            modifiers.fastForEach {
                                append("\n\t\t")
                                if (it.id in selectedModifiersBonuses)
                                    withStyle(
                                        LocalTextStyle.current
                                            .copy(textDecoration = TextDecoration.LineThrough)
                                            .toSpanStyle()
                                    ) {
                                        append(it.modifier.toSignedString())
                                    }
                                else
                                    append(it.modifier.toSignedString())
                                append(' ')
                            }
                        }
                        append('\n')
                    }
                }.let {
                    it.ifBlank { AnnotatedString(stringResource(Res.string.no_modifiers_for_info)) }
                }
            )
        }
    }
}
