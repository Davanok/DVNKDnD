package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.newEntity.newCharacter.NewCharacterTopBarAdditionalContent
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.new_character
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
    displaySkills: Map<Skills, UiSkillState>,
    onSelectSkill: (Skills) -> Unit
) {
    val statsAsModifiers = remember(stats) { stats.toModifiersList() }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            Skills.entries,
            key = { it }
        ) { skill ->
            SkillItem(
                statModifier = statsAsModifiers.fastFirst { it.stat == skill.stat }.modifier,
                proficiencyBonus = proficiencyBonus,
                skill = skill,
                state = displaySkills[skill],
                onClick = onSelectSkill,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
private fun SkillItem(
    statModifier: Int,
    proficiencyBonus: Int,
    skill: Skills,
    state: UiSkillState?,
    onClick: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.then(
            Modifier.toggleable(
                role = Role.Checkbox,
                enabled = state != null,
                value = state?.selected == true,
                onValueChange = { onClick(skill) }
            )
        ),
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(skill.stringRes))
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = (
                            calculateModifier(statModifier) +
                                    if (state?.selected == true) proficiencyBonus
                                    else 0
                            ).toSignedString())
            }
                          },
        trailingContent = {
            if (state == null) Spacer(modifier = Modifier.width(24.dp))
            else Checkbox(
                modifier = Modifier.width(24.dp),
                onCheckedChange = { onClick(skill) },
                checked = state.selected,
                enabled = state.selectable
            )
        }
    )
}
