package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
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
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.new_character
import dvnkdnd.composeapp.generated.resources.proficiency_bonus_value
import dvnkdnd.composeapp.generated.resources.selected_value
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewCharacterSkillsScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterSkillsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UiToaster(
        message = uiState.error?.toUiMessage(),
        onRemoveMessage = viewModel::removeWarning
    )
    when {
        uiState.isLoading -> LoadingCard()
        uiState.error is UiError.Critical -> ErrorCard(
            text = stringResource(uiState.error!!.message),
            exception = uiState.error!!.exception,
            onBack = onBack
        )
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
                proficiencyBonus = uiState.proficiencyBonus,
                stats = uiState.stats,
                selectionLimit = uiState.selectionLimit,
                displaySkills = uiState.skills,
                onSelectSkill = viewModel::selectSkill
            )
        }
    }
}
@Composable
private fun Content(
    proficiencyBonus: Int,
    stats: DnDModifiersGroup,
    selectionLimit: Int,
    displaySkills: Map<Skills, UiSelectableState>,
    onSelectSkill: (Skills) -> Unit
) {
    val skillsByStat = remember(displaySkills) {
        Skills.entries.groupBy { it.stat }.entries.sortedBy { (stat, _) -> stat.ordinal }
    }
    val selectedCount = remember(displaySkills) {
        displaySkills.count { it.value.selected }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            Res.string.selected_value,
                            selectedCount, selectionLimit
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(
                            Res.string.proficiency_bonus_value,
                            proficiencyBonus.toSignedString()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        skillsByStat.fastForEach { (stat, skills) ->
            stickyHeader {
                StatHeader(stat = stat, statModifier = stats[stat])
            }

            items(
                items = skills,
                key = { it }
            ) { skill ->
                SkillItem(
                    statModifier = stats[stat],
                    proficiencyBonus = proficiencyBonus,
                    skill = skill,
                    state = displaySkills[skill],
                    onClick = onSelectSkill,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatHeader(stat: Stats, statModifier: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(stat.stringRes),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = statModifier.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SkillItem(
    statModifier: Int,
    proficiencyBonus: Int,
    skill: Skills,
    state: UiSelectableState?,
    onClick: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = state?.selected == true
    val selectable = state?.selectable == true

    ListItem(
        modifier = modifier
            .then(
                Modifier
                    .toggleable(
                        value = selected,
                        enabled = state != null,
                        role = Role.Checkbox,
                        onValueChange = { onClick(skill) }
                    )
            ),
        headlineContent = {
            Text(text = stringResource(skill.stringRes))
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (
                            calculateModifier(statModifier) + if (selected) proficiencyBonus else 0
                            ).toSignedString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
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
    )
}
