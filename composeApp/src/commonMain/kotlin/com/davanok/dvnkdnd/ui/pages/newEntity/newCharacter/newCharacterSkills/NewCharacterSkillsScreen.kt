package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.navigation.StepNavigation
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
        else -> StepNavigation (
            modifier = Modifier.fillMaxSize(),
            next = { viewModel.commit(onContinue) },
            previous = onBack
        ) {
            Content(
                displaySkills = uiState.skills,
                onSelectSkill = viewModel::selectSkill
            )
        }
    }
}
@Composable
private fun Content(
    displaySkills: Map<Skills, UiSkillState>,
    onSelectSkill: (Skills) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            Skills.entries,
            key = { it }
        ) { skill ->
            SkillItem(
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
    skill: Skills,
    state: UiSkillState?,
    onClick: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.then(
            Modifier.clickable(
                enabled = state != null,
                onClick = { onClick(skill) }
            )
        ),
        headlineContent = { Text(text = stringResource(skill.stringRes)) },
        leadingContent = {
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
