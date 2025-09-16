package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrowsScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.dndEnums.applyForStringPreview
import com.davanok.dvnkdnd.data.model.dndEnums.skills
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import com.davanok.dvnkdnd.ui.components.newEntity.newCharacter.NewCharacterTopBarAdditionalContent
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.new_character
import dvnkdnd.composeapp.generated.resources.saving_throw
import dvnkdnd.composeapp.generated.resources.skills
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid


@Composable
fun ModifierExtendedInfo.buildString(): String {
    val valueString = if (valueSource == DnDModifierValueSource.CONST) {
        val unaryOps = setOf(DnDModifierOperation.ABS, DnDModifierOperation.ROUND, DnDModifierOperation.CEIL, DnDModifierOperation.FLOOR, DnDModifierOperation.FACT)
        if (operation in unaryOps && modifier.value == 0.0) null
        else modifier.value.toString()
    } else {
        // valueSource has a string resource; convert to the localized string
        stringResource(valueSource.stringRes)
    }

    return operation.applyForStringPreview(valueString)
}


private val StatItemMinWidth = 200.dp


@Composable
fun NewCharacterStatsLargeScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterThrowsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiToaster(
        message = uiState.error?.toUiMessage(),
        onRemoveMessage = viewModel::removeError
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
            title = uiState.character.name
                .ifBlank { stringResource(Res.string.new_character) },
            additionalContent = {
                NewCharacterTopBarAdditionalContent(uiState.character) {

                }
            },
            onNextClick = { viewModel.commit(onContinue) },
            onBackClick = onBack,
        ) {
            Content(
                attributes = uiState.attributes,
                savingThrows = uiState.savingThrows.mapValues { (_, v) -> v.first },
                savingThrowValues = uiState.savingThrows.mapValues { (_, v) -> v.second },
                skills = uiState.skills.mapValues { (_, v) -> v.first },
                skillsValues = uiState.skills.mapValues { (_, v) -> v.second },
                onSelectSavingThrow = viewModel::selectSavingThrow,
                onSelectSkill = viewModel::selectSkill
            )
        }
    }
}

@Composable
private fun Content(
    attributes: DnDAttributesGroup,
    savingThrows: Map<Attributes, List<ModifierExtendedInfo>>,
    savingThrowValues: Map<Attributes, Int>,
    skills: Map<Skills, List<ModifierExtendedInfo>>,
    skillsValues: Map<Skills, Int>,
    onSelectSavingThrow: (Uuid) -> Unit,
    onSelectSkill: (Uuid) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(StatItemMinWidth),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(
            items = Attributes.entries,
            key = { it }
        ) { attribute ->
            AttributeItem(
                attribute = attribute,
                attributeValue = attributes[attribute],
                savingThrowModifiers = savingThrows[attribute]?: emptyList(),
                savingThrowValue = savingThrowValues[attribute]?: 0,
                skillsModifiers = skills,
                skillsValues = skillsValues,
                onSelectSavingThrow = onSelectSavingThrow,
                onSelectSkill = onSelectSkill
            )
        }
    }
}

@Composable
fun AttributeItem(
    attribute: Attributes,
    attributeValue: Int,
    savingThrowModifiers: List<ModifierExtendedInfo>,
    savingThrowValue: Int,
    skillsModifiers: Map<Skills, List<ModifierExtendedInfo>>,
    skillsValues: Map<Skills, Int>,
    onSelectSavingThrow: (Uuid) -> Unit,
    onSelectSkill: (Uuid) -> Unit,
) {
    val calculatedModifier = remember { calculateModifier(attributeValue) }

    val compactView = remember(skillsModifiers) { skillsModifiers.values.all { it.size <= 1 } }

    OutlinedCard {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(attribute.stringRes),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier,
                    text = calculatedModifier.toSignedString(),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
            }
            // saving throw: value
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.saving_throw))
                Text(
                    text = savingThrowValue.toSignedString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // saving throws modifiers
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                savingThrowModifiers.fastForEach { modifier ->
                    ModifierChip(
                        modifier = modifier,
                        onClick = onSelectSavingThrow
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            // skills
            Text(
                text = stringResource(Res.string.skills),
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attribute.skills().fastForEach { skill ->
                    val modifiers = skillsModifiers[skill] ?: emptyList()

                    if (compactView) {
                        val modifier = modifiers.firstOrNull()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .then(
                                    if (modifier == null) Modifier
                                    else Modifier.toggleable(
                                        value = modifier.state.selected,
                                        enabled = modifier.state.selectable,
                                        role = Role.Checkbox,
                                        onValueChange = { onSelectSkill(modifier.modifier.id) }
                                    )
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(skill.stringRes),
                                maxLines = 1
                            )
                            modifier?.let { modifier ->
                                ModifierChip(modifier, onSelectSkill)
                            }
                            Text(
                                text = skillsValues[skill]?.toSignedString() ?: "0",
                                maxLines = 1
                            )
                        }
                    }
                    else {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(skill.stringRes),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                Text(
                                    text = skillsValues[skill]?.toSignedString() ?: "0",
                                    maxLines = 1
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                modifiers.fastForEach { modifier ->
                                    ModifierChip(modifier, onSelectSkill)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModifierChip(modifier: ModifierExtendedInfo, onClick: (Uuid) -> Unit) {
    FilterChip(
        selected = modifier.state.selected,
        onClick = { if (modifier.state.selectable) onClick(modifier.modifier.id) },
        label = { Text(modifier.buildString()) },
        enabled = modifier.state.selectable,
    )
}