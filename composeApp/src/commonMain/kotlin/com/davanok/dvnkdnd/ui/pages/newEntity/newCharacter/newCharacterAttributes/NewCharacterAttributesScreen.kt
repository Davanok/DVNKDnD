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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.applyForString
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers_selectors
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.continue_str
import dvnkdnd.composeapp.generated.resources.modifiers_selectors_hint
import dvnkdnd.composeapp.generated.resources.new_character_attributes_screen_title
import dvnkdnd.composeapp.generated.resources.no_modifiers_for_info
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCharacterAttributesScreen(
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
    allModifiersGroups: List<ModifiersGroup>,
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
            modifiers = modifiers,
            onModifiersChange = onModifiersChange,
            onSelectModifiers = onSelectModifier
        )
    }
    if (showInfoDialog)
        AboutModifiersSelectorsDialog(
            allModifiersGroups = allModifiersGroups,
            selectedModifiersBonuses = selectedModifiersBonuses,
            onDismiss = { showInfoDialog = false }
        )
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
    allModifiersGroups: List<ModifiersGroup>,
    selectedModifiersBonuses: Set<Uuid>,
    onDismiss: () -> Unit
) {
    val markdownString = stringResource(Res.string.modifiers_selectors_hint)

    AdaptiveModalSheet(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.about_modifiers_selectors)) }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
        ) {
            Markdown(content = markdownString)

            Spacer(modifier = Modifier.height(12.dp))

            val infoText = buildAnnotatedString {
                allModifiersGroups.sortedBy { it.priority }.fastForEach { group ->
                    if (group.modifiers.isEmpty()) return@fastForEach
                    withStyle(MaterialTheme.typography.labelLarge.toSpanStyle()) {
                        append(group.name)
                    }
                    group.modifiers
                        .groupBy { it.targetAs<Attributes>() }
                        .forEach { (attribute, modifiers) ->
                            if (attribute == null) return@forEach

                            val attributeStr = stringResource(attribute.stringRes)
                            append("\n\t")
                            append(attributeStr)
                            modifiers.fastForEach {
                                append("\n\t\t")
                                val textToAppend = group.operation.applyForString(attributeStr, group.value)
                                if (it.id in selectedModifiersBonuses) {
                                    withStyle(
                                        LocalTextStyle.current
                                            .copy(textDecoration = TextDecoration.Underline)
                                            .toSpanStyle()
                                    ) {
                                        append(textToAppend)
                                    }
                                } else {
                                    append(textToAppend)
                                }
                                append(' ')
                            }
                        }
                    append('\n')
                }
            }

            Text(
                text = infoText.ifBlank { AnnotatedString(stringResource(Res.string.no_modifiers_for_info)) }
            )
        }
    }
}
