package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStatsLargeScreen

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.ui.UiSelectableState
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import com.davanok.dvnkdnd.ui.components.newEntity.newCharacter.NewCharacterStatsAdditionalContent
import com.davanok.dvnkdnd.ui.components.newEntity.newCharacter.NewCharacterTopBarAdditionalContent
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.new_character
import dvnkdnd.composeapp.generated.resources.saving_throw
import dvnkdnd.composeapp.generated.resources.selected_value
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


private val StatItemMinWidth = 200.dp


@Composable
fun NewCharacterStatsLargeScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterStatsLargeViewModel = koinViewModel()
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
                    val savingThrowsSelectedCount = remember(uiState.savingThrows) {
                        uiState.savingThrows.count { it.value.selected }
                    }
                    val skillsSelectedCount = remember(uiState.savingThrows) {
                        uiState.savingThrows.count { it.value.selected }
                    }
                    NewCharacterStatsAdditionalContent(
                        selectedCount = savingThrowsSelectedCount,
                        selectionLimit = uiState.savingThrowsSelectionLimit,
                        proficiencyBonus = uiState.proficiencyBonus
                    )
                    Text(
                        text = stringResource(
                            Res.string.selected_value,
                            skillsSelectedCount,
                            uiState.skillsSelectionLimit
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            onNextClick = { viewModel.commit(onContinue) },
            onBackClick = onBack,
        ) {
            Content(
                stats = uiState.stats,
                proficiencyBonus = uiState.proficiencyBonus,
                savingThrows = uiState.savingThrows,
                skills = uiState.skills,
                onSelectSavingThrow = viewModel::selectSavingThrow,
                onSelectSkill = viewModel::selectSkill
            )
        }
    }
}

@Composable
private fun Content(
    stats: DnDAttributesGroup,
    proficiencyBonus: Int,
    savingThrows: Map<Attributes, UiSelectableState>,
    skills: Map<Skills, UiSelectableState>,
    onSelectSavingThrow: (Attributes) -> Unit,
    onSelectSkill: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val statToSkillStates = remember(skills) {
        Attributes.entries
            .associateWith { stat ->
                Skills.entries
                    .fastFilter { it.stat == stat }
                    .associateWith { skills[it] ?: UiSelectableState.OfFalse }
            }
    }
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
        ) { stat ->
            StatItem(
                stat = stat,
                statModifier = stats[stat],
                proficiencyBonus = proficiencyBonus,
                savingThrowState = savingThrows[stat] ?: UiSelectableState.OfFalse,
                skillState = statToSkillStates[stat]!!,
                onSelectSavingThrow = onSelectSavingThrow,
                onSelectSkill = onSelectSkill,
                modifier = Modifier
                    .aspectRatio(0.75f)
            )
        }
    }
}

@Composable
fun StatItem(
    stat: Attributes,
    statModifier: Int,
    proficiencyBonus: Int,
    savingThrowState: UiSelectableState,
    skillState: Map<Skills, UiSelectableState>,
    onSelectSavingThrow: (Attributes) -> Unit,
    onSelectSkill: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val calculatedModifier = remember { calculateModifier(statModifier) }
    OutlinedCard(
        modifier = modifier
    ) {
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
                    text = stringResource(stat.stringRes),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier,
                    text = statModifier.toSignedString(),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = savingThrowState.selected,
                    onClick = { if (savingThrowState.selectable) onSelectSavingThrow(stat) },
                    label = { Text(stringResource(Res.string.saving_throw)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null)
                    },
                    enabled = savingThrowState.selectable,
                )
                Text(
                    text = calculatedModifier.toSignedString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (skillState.isNotEmpty()) {
                Text(text = "Skills", style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skillState.forEach { (skill, state) ->
                        val finalModifier = calculatedModifier + if (state.selected) proficiencyBonus else 0

                        Row(
                            modifier = Modifier
                                .height(24.dp)
                                .fillMaxWidth()
                                .toggleable(
                                    value = state.selected,
                                    enabled = state.selectable,
                                    role = Role.Checkbox,
                                    onValueChange = { onSelectSkill(skill) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(skill.stringRes),
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Text(
                                text = finalModifier.toSignedString(),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                            Box(
                                modifier = Modifier.width(24.dp),
                            ) {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = state.fixedSelection || state.selectable,
                                    enter = fadeIn() + scaleIn(),
                                    exit = scaleOut() + fadeOut()
                                ) {
                                    Checkbox(
                                        checked = state.selected,
                                        onCheckedChange = null,
                                        enabled = !state.fixedSelection
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}