package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
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
import dvnkdnd.composeapp.generated.resources.new_character_saving_throws_screen_title
import org.jetbrains.compose.resources.stringResource


@Composable
fun NewCharacterSavingThrowsScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterSavingThrowsViewModel
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
            title = stringResource(Res.string.new_character_saving_throws_screen_title),
            additionalContent = {
                NewCharacterTopBarAdditionalContent(uiState.character) {
                    val selectedCount = remember(uiState.savingThrows) {
                        uiState.savingThrows.count { it.value.selected }
                    }
                    NewCharacterStatsAdditionalContent(
                        selectedCount = selectedCount,
                        selectionLimit = uiState.selectionLimit,
                        proficiencyBonus = uiState.proficiencyBonus
                    )
                }
            },
            onNextClick = { viewModel.commit(onContinue) },
            onBackClick = onBack,
        ) {
            Content(
                proficiencyBonus = uiState.proficiencyBonus,
                stats = uiState.stats,
                displaySavingThrows = uiState.savingThrows,
                onSelectSavingThrow = viewModel::selectSavingThrow
            )
        }
    }
}

@Composable
private fun Content(
    proficiencyBonus: Int,
    stats: DnDModifiersGroup,
    displaySavingThrows: Map<Stats, UiSelectableState>,
    onSelectSavingThrow: (Stats) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = Stats.entries,
            key = { it }
        ) { stat ->
            StatItem(
                statModifier = stats[stat],
                proficiencyBonus = proficiencyBonus,
                stat = stat,
                state = displaySavingThrows[stat] ?: UiSelectableState.OfFalse,
                onClick = onSelectSavingThrow,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
@Composable
private fun StatItem(
    statModifier: Int,
    proficiencyBonus: Int,
    stat: Stats,
    state: UiSelectableState,
    onClick: (Stats) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .then(
                Modifier
                    .toggleable(
                        value = state.selected,
                        enabled = state.selectable,
                        role = Role.Checkbox,
                        onValueChange = { onClick(stat) }
                    )
            ),
        headlineContent = {
            Text(text = stringResource(stat.stringRes))
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (
                            calculateModifier(statModifier) + if (state.selected) proficiencyBonus else 0
                            ).toSignedString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
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
    )
}