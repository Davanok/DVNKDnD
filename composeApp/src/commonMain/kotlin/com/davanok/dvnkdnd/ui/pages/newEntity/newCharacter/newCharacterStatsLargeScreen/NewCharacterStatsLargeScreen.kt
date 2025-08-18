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
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.character.DnDEntityWithSavingThrows
import com.davanok.dvnkdnd.data.model.entities.character.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSavingThrow
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkill
import com.davanok.dvnkdnd.data.model.types.UiSelectableState
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import com.davanok.dvnkdnd.ui.components.newEntity.newCharacter.NewCharacterTopBarAdditionalContent
import com.davanok.dvnkdnd.ui.components.toSignedString
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows.SavingThrowsTableState
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills.SkillsTableState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.new_character
import dvnkdnd.composeapp.generated.resources.saving_throw
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid


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
        uiState.error is UiError.Critical -> uiState.error?.let {
            ErrorCard(
                text = stringResource(it.message),
                exception = it.exception,
                onBack = onBack
            )
        }

        else -> NewEntityStepScaffold (
            modifier = Modifier.fillMaxSize(),
            title = uiState.character.name
                .ifBlank { stringResource(Res.string.new_character) },
            additionalContent = uiState.character
                .takeUnless { it.isBlank() }?.let {
                    { NewCharacterTopBarAdditionalContent(it) }
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
    stats: DnDModifiersGroup,
    proficiencyBonus: Int,
    savingThrows: Map<Stats, UiSelectableState>,
    skills: Map<Skills, UiSelectableState>,
    onSelectSavingThrow: (Stats) -> Unit,
    onSelectSkill: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val statToSkillStates = remember {
        Stats.entries
            .associateWith { stat ->
                Skills.entries
                    .fastFilter { it.stat == stat }
                    .associateWith { skills[it] }
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
            items = Stats.entries,
            key = { it }
        ) { stat ->
            StatItem(
                stat = stat,
                statModifier = stats[stat],
                proficiencyBonus = proficiencyBonus,
                savingThrowState = savingThrows[stat],
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
    stat: Stats,
    statModifier: Int,
    proficiencyBonus: Int,
    savingThrowState: UiSelectableState?,
    skillState: Map<Skills, UiSelectableState?>,
    onSelectSavingThrow: (Stats) -> Unit,
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
                val sState = savingThrowState ?: UiSelectableState(selectable = false, selected = false)
                FilterChip(
                    selected = sState.selected,
                    onClick = { if (sState.selectable) onSelectSavingThrow(stat) },
                    label = { Text(stringResource(Res.string.saving_throw)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null)
                    },
                    enabled = sState.selectable,
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
                        val selected = state?.selected == true
                        val selectable = state?.selectable == true
                        val finalModifier = calculatedModifier + if (selected) proficiencyBonus else 0

                        Row(
                            modifier = Modifier
                                .height(24.dp)
                                .fillMaxWidth()
                                .toggleable(
                                    value = selected,
                                    enabled = state != null,
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
                                    visible = selectable || selected,
                                    enter = fadeIn() + scaleIn(),
                                    exit = scaleOut() + fadeOut()
                                ) {
                                    Checkbox(
                                        checked = selected,
                                        onCheckedChange = null
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
@Composable
fun NewCharacterStatsLargeScreenPreview() {
    val savingThrowsState = remember {
        val savingThrows = listOf(
            Stats.STRENGTH to UiSelectableState(false, true),
            Stats.DEXTERITY to UiSelectableState(true, false),
            Stats.INTELLIGENCE to UiSelectableState(true, false)
        ).map { (stat, state) ->
            DnDSavingThrow(
                Uuid.random(),
                state.selectable,
                stat
            )
        }
        SavingThrowsTableState(
            columns = listOf(
                DnDEntityWithSavingThrows(
                    entity = DnDEntityMin(Uuid.NIL, DnDEntityTypes.CLASS, "123", "123"),
                    selectionLimit = 2,
                    savingThrows = savingThrows
                )
            ),
            initialSelectedSavingThrow = savingThrows.filter { !it.selectable }.map { it.id }.toSet()
        )
    }
    val skillsState = remember {
        val skills = listOf(
            Skills.ACROBATICS to UiSelectableState(false, true),
            Skills.ANIMAL_HANDLING to UiSelectableState(false, true),
            Skills.ATHLETICS to UiSelectableState(true, false),
            Skills.HISTORY to UiSelectableState(true, false),
            Skills.INSIGHT to UiSelectableState(true, false),
            Skills.INTIMIDATION to UiSelectableState(true, false),
            Skills.PERCEPTION to UiSelectableState(true, false),
            Skills.SURVIVAL to UiSelectableState(true, false),
        ).map { (skill, state) ->
            DnDSkill(
                Uuid.random(),
                state.selectable,
                skill
            )
        }
        SkillsTableState(
            columns = listOf(
                DnDEntityWithSkills(
                    entity = DnDEntityMin(Uuid.NIL, DnDEntityTypes.CLASS, "123", "123"),
                    selectionLimit = 5,
                    skills = skills
                )
            ),
            initialSelectedSkills = skills.filter { !it.selectable }.map { it.id }.toSet()
        )
    }

    Content(
        stats = DnDModifiersGroup(17, 15, 14, 13, 12, 10),
        proficiencyBonus = 2,
        savingThrows = savingThrowsState.getDisplayItems(),
        skills = skillsState.getDisplayItems(),
        onSelectSavingThrow = {
            savingThrowsState.select(it)
        },
        onSelectSkill = {
            skillsState.select(it)
        }
    )
}