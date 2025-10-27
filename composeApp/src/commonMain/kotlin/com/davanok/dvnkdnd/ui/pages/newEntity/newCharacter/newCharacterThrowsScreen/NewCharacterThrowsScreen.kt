package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrowsScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.applyForStringPreview
import com.davanok.dvnkdnd.data.model.dndEnums.skills
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.types.ModifierExtendedInfo
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.continue_str
import dvnkdnd.composeapp.generated.resources.new_character_throws_screen_title
import dvnkdnd.composeapp.generated.resources.saving_throw
import dvnkdnd.composeapp.generated.resources.skills
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid


private val StatItemMinWidth = 200.dp


@OptIn(ExperimentalMaterial3Api::class)
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

        else -> Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(Res.string.new_character_throws_screen_title))
                    },
                    navigationIcon = {
                        IconButton(onBack) {
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
                modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
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
    var itemsMaxHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(StatItemMinWidth),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = Attributes.entries,
            key = { it }
        ) { attribute ->
            AttributeItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (itemsMaxHeight > 0) Modifier.height(density.run { itemsMaxHeight.toDp() })
                        else Modifier
                    )
                    .onGloballyPositioned {
                        val h = it.size.height
                        if (h > itemsMaxHeight) {
                            itemsMaxHeight = h
                        }
                    },
                attribute = attribute,
                attributeValue = attributes[attribute],
                savingThrowModifiers = savingThrows[attribute] ?: emptyList(),
                savingThrowValue = savingThrowValues[attribute] ?: 0,
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
    modifier: Modifier = Modifier
) {
    val compactView = remember(skillsModifiers) { skillsModifiers.values.all { it.size <= 1 } }

    OutlinedCard(modifier = modifier) {
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
                    text = buildString {
                        append(attributeValue)
                        append(" (")
                        append(calculateModifier(attributeValue).toSignedString())
                        append(')')
                    },
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
                savingThrowModifiers.fastForEach { modInfo ->
                    ModifierChip(
                        info = modInfo,
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
                    val modifiersList = skillsModifiers[skill] ?: emptyList()

                    if (compactView) {
                        val firstModInfo = modifiersList.firstOrNull()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .then(
                                    if (firstModInfo == null) Modifier
                                    else Modifier.toggleable(
                                        value = firstModInfo.state.selected,
                                        enabled = firstModInfo.state.selectable,
                                        role = Role.Checkbox,
                                        onValueChange = { onSelectSkill(firstModInfo.modifier.id) }
                                    )
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(skill.stringRes),
                                maxLines = 1
                            )
                            firstModInfo?.let { modInfo ->
                                ModifierChip(modInfo, onSelectSkill)
                            }
                            Text(
                                text = skillsValues[skill]?.toSignedString() ?: "0",
                                maxLines = 1
                            )
                        }
                    } else
                        Column(
                            modifier = Modifier.height(24.dp)
                        ) {
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
                                modifiersList.fastForEach { modInfo ->
                                    ModifierChip(modInfo, onSelectSkill)
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun ModifierChip(info: ModifierExtendedInfo, onClick: (Uuid) -> Unit) {
    FilterChip(
        selected = info.state.selected,
        onClick = { if (info.state.selectable) onClick(info.modifier.id) },
        label = { Text(info.buildString()) },
        enabled = info.state.selectable,
    )
}

@Composable
fun ModifierExtendedInfo.buildString(): String {
    val valueString =
        if (valueSource == DnDModifierValueSource.CONSTANT) {
            val unaryOps = setOf(
                DnDModifierOperation.ABS,
                DnDModifierOperation.ROUND,
                DnDModifierOperation.CEIL,
                DnDModifierOperation.FLOOR,
                DnDModifierOperation.FACT
            )
            if (operation in unaryOps && value == 0.0) null
            else value.toString()
        } else
            stringResource(valueSource.stringRes)

    return operation.applyForStringPreview(valueString)
}