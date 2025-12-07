package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.ui.model.isCritical
import com.davanok.dvnkdnd.ui.components.EmptyImage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.navigation.FABScaffold
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.character_image
import dvnkdnd.composeapp.generated.resources.no_characters_yet
import dvnkdnd.composeapp.generated.resources.select_character_for_info
import dvnkdnd.composeapp.generated.resources.sentiment_dissatisfied
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CharactersListScreen(
    onNewCharacter: () -> Unit,
    navigateToCharacter: (CharacterBase) -> Unit,
    viewModel: CharactersListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            FABScaffold(onClick = onNewCharacter) {
                ErrorCard(
                    text = it.message,
                    exception = it.exception,
                )
            }
        }
        uiState.characters.isEmpty() ->
            FABScaffold(onClick = onNewCharacter) {
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
        else -> Content(
            characters = uiState.characters,
            onNewCharacter = onNewCharacter,
            navigateToCharacter = navigateToCharacter,
            onClickCharacter = viewModel::selectCharacter,
            currentCharacter = uiState.currentCharacter,
            isCurrentCharacterLoading = uiState.isCurrentCharacterLoading
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    characters: List<CharacterBase>,
    onNewCharacter: () -> Unit,
    onClickCharacter: (CharacterBase) -> Unit,
    navigateToCharacter: (CharacterBase) -> Unit,
    currentCharacter: CharacterFull?,
    isCurrentCharacterLoading: Boolean,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()

    val characterVisible = navigator.scaffoldState.currentState[ThreePaneScaffoldRole.Primary] == PaneAdaptedValue.Expanded
    val listVisible = navigator.scaffoldState.currentState[ThreePaneScaffoldRole.Secondary] == PaneAdaptedValue.Expanded

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = !characterVisible || currentCharacter == null
                    ) { showAppName ->
                        if (showAppName || currentCharacter == null)
                            Text(text = stringResource(Res.string.app_name))
                        else
                            Text(text = currentCharacter.character.name)
                    }
                },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = !listVisible,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                        exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
                    ) {
                        IconButton(
                            onClick = { coroutineScope.launch { navigator.navigateBack() } },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(Res.string.back)
                            )
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = characterVisible && currentCharacter != null,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                        exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
                    ) {
                        IconButton(
                            onClick = { currentCharacter?.let { navigateToCharacter(it.character) } },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                contentDescription = stringResource(Res.string.back)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        ListDetailPaneScaffold(
            modifier = Modifier.padding(paddingValues),
            directive = navigator.scaffoldDirective,
            scaffoldState = navigator.scaffoldState,
            listPane = {
                AnimatedPane {
                    FABScaffold(
                        onClick = onNewCharacter
                    ) {
                        CharactersList(
                            modifier = Modifier.fillMaxSize(),
                            items = characters,
                            onClick = { character ->
                                onClickCharacter(character)
                                coroutineScope.launch {
                                    navigator.navigateTo(
                                        ListDetailPaneScaffoldRole.Detail
                                    )
                                }
                            },
                            onLongClick = { character ->
                                onClickCharacter(character)
                                coroutineScope.launch {
                                    navigator.navigateTo(
                                        ListDetailPaneScaffoldRole.Detail
                                    )
                                }
                                navigateToCharacter(character)
                            }
                        )
                    }
                }
            },
            detailPane = {
                AnimatedPane {
                    when {
                        isCurrentCharacterLoading -> Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        currentCharacter == null -> FullScreenCard {
                            Text(stringResource(Res.string.select_character_for_info))
                        }
                        else -> CharacterInfo(currentCharacter)
                    }
                }
            }
        )
    }
}

@Composable
private fun CharactersList(
    items: List<CharacterBase>,
    onClick: (CharacterBase) -> Unit,
    onLongClick: (CharacterBase) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row {
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = items,
                key = { it.id }
            ) { character ->
                CharacterItem(
                    character = character,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        VerticalDivider(Modifier.fillMaxHeight())
    }
}

@Composable
private fun CharacterItem(
    character: CharacterBase,
    onClick: (CharacterBase) -> Unit,
    onLongClick: (CharacterBase) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
            .combinedClickable(
                onLongClick = { onLongClick(character) },
                onDoubleClick = { onLongClick(character) },
                onClick = { onClick(character) }
            ),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterInfo(
    character: CharacterFull
) {

}
