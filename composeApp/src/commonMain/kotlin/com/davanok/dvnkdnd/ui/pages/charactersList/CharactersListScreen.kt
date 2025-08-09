package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.ui.components.EmptyImage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.adaptive.TwoPane
import com.davanok.dvnkdnd.ui.navigation.FABScaffold
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_image
import dvnkdnd.composeapp.generated.resources.no_characters_yet
import dvnkdnd.composeapp.generated.resources.sentiment_dissatisfied
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CharactersListScreen(
    onNewCharacter: () -> Unit,
    navigateToCharacter: (CharacterMin) -> Unit,
    viewModel: CharactersListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.error is UiError.Critical) ErrorCard(
        text = stringResource(uiState.error!!.message),
        exception = uiState.error!!.exception,
        onRefresh = viewModel::loadCharacters
    )
    else
        TwoPane(
            modifier = Modifier.fillMaxSize(),
            firstPane = { twoPane ->
                FABScaffold(
                    onClick = onNewCharacter
                ) {
                    FirstPaneContent(
                        modifier = Modifier.fillMaxSize(),
                        isLoading = uiState.isLoading,
                        characters = uiState.characters,
                        onClick = { character ->
                            if (twoPane) viewModel.selectCharacter(character)
                            else navigateToCharacter(character)
                        }
                    )
                }
            },
            secondPane = { CharacterShortInfo(uiState.currentCharacter, viewModel) }
        )
}

@Composable
private fun FirstPaneContent(
    isLoading: Boolean,
    characters: List<CharacterMin>,
    onClick: (CharacterMin) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading -> Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        characters.isEmpty() -> Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) { EmptyScreen() }

        else -> CharactersList(
            characters,
            onClick,
            modifier
        )
    }
}

@Composable
private fun EmptyScreen() {
    FullScreenCard(
        heroIcon = {
            Icon(
                painter = painterResource(Res.drawable.sentiment_dissatisfied),
                contentDescription = stringResource(Res.string.no_characters_yet)
            )
        },
        content = {
            Text(
                text = stringResource(Res.string.no_characters_yet)
            )
        }
    )
}

@Composable
private fun CharactersList(
    items: List<CharacterMin>,
    onClick: (CharacterMin) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = items,
            key = { it.id }
        ) { character ->
            CharacterCard(
                character = character,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CharacterCard(
    character: CharacterMin,
    onClick: (CharacterMin) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable { onClick(character) },
        headlineContent = { Text(character.name) },
        leadingContent = {
            if (character.image == null) EmptyImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                text = character.name
            )
            else AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                model = character.image,
                contentDescription = stringResource(Res.string.character_image)
            )
        }
    )
}

@Composable
private fun CharacterShortInfo(
    character: CharacterMin?,
    viewModel: CharactersListViewModel,
) {
    // TODO
    Box(modifier = Modifier.fillMaxSize().background(Color.Green))
}
