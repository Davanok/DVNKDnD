package com.davanok.dvnkdnd.ui.pages.charactersList.charactersList

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.adaptive.alternativeClickable
import com.davanok.dvnkdnd.ui.model.isCritical
import com.davanok.dvnkdnd.ui.navigation.NavigationFABScaffold
import dev.zacsweers.metrox.viewmodel.metroViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import dvnkdnd.composeapp.generated.resources.no_characters_yet
import dvnkdnd.composeapp.generated.resources.sentiment_dissatisfied
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersListScreen(
    onNewCharacter: () -> Unit,
    navigateToCharacter: (CharacterMin) -> Unit,
    navigateToCharacterFull: (CharacterMin) -> Unit,
    viewModel: CharactersListViewModel = metroViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavigationFABScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.app_name))
                }
            )
        },
        onFABClick = onNewCharacter
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingCard(modifier = Modifier.padding(paddingValues))
            uiState.error.isCritical() -> uiState.error?.let {
                ErrorCard(
                    text = it.message,
                    exception = it.exception,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.characters.isEmpty() ->
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

            else -> Content(
                characters = uiState.characters,
                navigateToCharacter = navigateToCharacterFull,
                onClickCharacter = navigateToCharacter,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun Content(
    characters: List<CharacterMin>,
    onClickCharacter: (CharacterMin) -> Unit,
    navigateToCharacter: (CharacterMin) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            CharacterItem(
                character = character,
                onClick = onClickCharacter,
                onLongClick = navigateToCharacter,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CharacterItem(
    character: CharacterMin,
    onClick: (CharacterMin) -> Unit,
    onLongClick: (CharacterMin) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
            .alternativeClickable(
                onClick = { onClick(character) },
                onAlternativeClick = { onLongClick(character) }
            ),
        headlineContent = { Text(character.name) },
        leadingContent = {
            BaseEntityImage(
                character = character,
                modifier = Modifier
                    .size(56.dp)
            )
        }
    )
}