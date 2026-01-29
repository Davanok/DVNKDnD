package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.domain.entities.character.ValueModifiersGroupWithResolvedValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.model.isCritical
import com.davanok.dvnkdnd.ui.model.toUiMessage
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers_selectors
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.continue_str
import dvnkdnd.composeapp.generated.resources.modifiers_selectors_hint
import dvnkdnd.composeapp.generated.resources.new_character_attributes_screen_title
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCharacterAttributesScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterAttributesViewModel
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
        else -> Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(Res.string.new_character_attributes_screen_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(Res.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.commit(onContinue) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(Res.string.continue_str)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Content(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                selectedCreationOption = uiState.attributesSelectorType,
                onOptionSelected = viewModel::selectAttributeSelectorType,
                allModifiersGroups = uiState.allModifiersGroups,
                selectedModifiersBonuses = uiState.selectedAttributesBonuses,
                modifiers = uiState.modifiers,
                onModifiersChange = viewModel::setModifiers,
                onSelectModifier = viewModel::selectModifier
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    selectedCreationOption: AttributesSelectorType,
    onOptionSelected: (AttributesSelectorType) -> Unit,
    allModifiersGroups: List<ValueModifiersGroupWithResolvedValues>,
    selectedModifiersBonuses: Set<Uuid>,
    modifiers: AttributesGroup,
    onModifiersChange: (AttributesGroup) -> Unit,
    onSelectModifier: (DnDModifier) -> Unit
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
    ) {
        CreationOptionsSelector(
            selectedCreationOption = selectedCreationOption,
            onOptionSelected = onOptionSelected,
            onInfoClick = { showInfoDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModifiersSelector(
            selectedCreationOption = selectedCreationOption,
            allModifiersGroups = allModifiersGroups,
            selectedAttributeModifiers = selectedModifiersBonuses,
            attributes = modifiers,
            onModifiersChange = onModifiersChange,
            onSelectModifiers = onSelectModifier
        )
    }
    if (showInfoDialog)
        AboutModifiersSelectorsDialog(onDismiss = { showInfoDialog = false })
}

@Composable
private fun CreationOptionsSelector(
    selectedCreationOption: AttributesSelectorType,
    onOptionSelected: (AttributesSelectorType) -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .weight(1f)
        ) {
            AttributesSelectorType.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = selectedCreationOption == option,
                    onClick = { onOptionSelected(option) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AttributesSelectorType.entries.size
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
            onClick = onInfoClick,
            modifier = Modifier.padding(start = 8.dp)
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
    onDismiss: () -> Unit
) {
    val markdownString = stringResource(Res.string.modifiers_selectors_hint)

    AdaptiveModalSheet(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.about_modifiers_selectors)) }
    ) {
        Markdown(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            content = markdownString)
    }
}
