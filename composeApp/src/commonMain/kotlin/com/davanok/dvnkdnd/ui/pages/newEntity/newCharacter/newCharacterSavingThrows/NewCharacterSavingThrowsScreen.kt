package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
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
                proficiencyBonus = uiState.proficiencyBonus,
                stats = uiState.stats,
                selectionLimit = uiState.selectionLimit,
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
    selectionLimit: Int,
    displaySavingThrows: Map<Stats, UiSavingThrowState>,
    onSelectSavingThrow: (Stats) -> Unit
) {
    val statsAsModifiers = remember(stats) { stats.toModifiersList() }
    val selectedCount = remember(displaySavingThrows) {
        displaySavingThrows.count { it.value.selected }
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
        items(
            items = Stats.entries,
            key = { it }
        ) { stat ->
            StatItem(
                statModifier = statsAsModifiers.fastFirst { it.stat == stat }.modifier,
                proficiencyBonus = proficiencyBonus,
                stat = stat,
                state = displaySavingThrows[stat],
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
    state: UiSavingThrowState?,
    onClick: (Stats) -> Unit,
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