package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterState
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.rememberCollapsingNestedScrollConnection
import com.davanok.dvnkdnd.ui.pages.characterFull.components.ImagesCarousel
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_character_state
import dvnkdnd.composeapp.generated.resources.character_has_no_states
import dvnkdnd.composeapp.generated.resources.state_from
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterStatesScreen(
    states: List<CharacterState>,
    onClick: (DnDFullEntity) -> Unit,
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
                modifier = Modifier.fillMaxSize()
            )
    }
}

@Composable
private fun CharacterStatesScreenContent(
    states: List<CharacterState>,
    onClick: (DnDFullEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var stateDialog: CharacterState? by remember { mutableStateOf(null) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = states,
            key = { it.state.entity.id }
        ) { state ->
            StateCard(
                state = state,
                onClick = { stateDialog = state },
                onOpenInfo = { onClick(state.state) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    stateDialog?.let {
        CharacterStateDialog(
            state = it,
            onSourceClick = onClick,
            onDismissRequest = { stateDialog = null }
        )
    }
}

@Composable
private fun StateCard(
    state: CharacterState,
    onClick: () -> Unit,
    onOpenInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entity = state.state.entity
    val fullState = state.state.state

    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
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

                fullState?.duration?.let { duration ->
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
}

@Composable
private fun CharacterStateDialog(
    state: CharacterState,
    onSourceClick: (DnDFullEntity) -> Unit,
    onDismissRequest: () -> Unit
) {
    val entity = state.state

    AdaptiveModalSheet(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = entity.entity.name)
        }
    ) {
        var additionalContentExpanded by remember { mutableStateOf(true) }
        val nestedScrollConnection = rememberCollapsingNestedScrollConnection {
            additionalContentExpanded = it
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (entity.images.isNotEmpty())
                AnimatedVisibility(
                    visible = additionalContentExpanded
                ) {
                    ImagesCarousel(
                        images = entity.images.map { it.path },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(nestedScrollConnection),
            ) {
                state.source?.let { source ->
                    Card(
                        onClick = { onSourceClick(source) }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(text = stringResource(Res.string.state_from))
                            Row {
                                BaseEntityImage(
                                    entity = source.toDnDEntityMin(),
                                    modifier = Modifier.size(56.dp)
                                )

                                Spacer(Modifier.width(12.dp))

                                Text(text = source.entity.name)
                            }
                        }
                    }
                }

                Markdown(state.state.entity.description)
            }

            state.state.state?.duration?.let { duration ->
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