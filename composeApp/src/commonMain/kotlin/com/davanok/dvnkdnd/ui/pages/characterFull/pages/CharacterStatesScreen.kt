package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterState
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.SwipeToActionBox
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_character_state
import dvnkdnd.composeapp.generated.resources.character_has_no_states
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterStatesScreen(
    states: List<CharacterState>,
    onClick: (DnDFullEntity) -> Unit,
    onDeleteStateClick: (CharacterState) -> Unit,
    onAddStateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStateClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.add_character_state)
                )
            }
        }
    ) {
        if (states.isEmpty())
            FullScreenCard {
                Text(text = stringResource(Res.string.character_has_no_states))
            }
        else
            CharacterStatesScreenContent(
                states = states,
                onClick = onClick,
                onDelete = onDeleteStateClick,
                modifier = Modifier.fillMaxSize()
            )
    }
}

@Composable
private fun CharacterStatesScreenContent(
    states: List<CharacterState>,
    onClick: (DnDFullEntity) -> Unit,
    onDelete: (CharacterState) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = states,
            key = { it.state.entity.id },
            contentType = { it.deletable }
        ) { state ->
            if (state.deletable) {
                SwipeToActionBox(
                    onDismiss = { onDelete(state) },
                    actionIcon = {
                        Icon(Icons.Default.Delete, null)
                    },
                    modifier = Modifier.animateItem()
                ) {
                    StateCard(
                        state = state,
                        onOpenInfo = { onClick(state.state) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                StateCard(
                    state = state,
                    onOpenInfo = { onClick(state.state) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StateCard(
    state: CharacterState,
    onOpenInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entity = state.state.entity

    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row {
                BaseEntityImage(
                    entity = state.state.toDnDEntityMin(),
                    onClick = onOpenInfo,
                    modifier = Modifier
                        .size(56.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = entity.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    state.state.state?.let { fullState ->
                        Text(text = stringResource(fullState.type.stringRes))

                        fullState.duration?.let { duration ->
                            Text(
                                text = buildString {
                                    if (duration.timeUnitsCount > 1)
                                        append(duration.timeUnitsCount)
                                    append(' ')
                                    append(stringResource(duration.timeUnit.stringRes))
                                }
                            )
                        }
                    }
                }
            }
            if (entity.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = entity.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}