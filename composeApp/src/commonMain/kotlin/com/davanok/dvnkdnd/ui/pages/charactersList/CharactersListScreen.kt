package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.ui.components.EmptyImage
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.navigation.FABScaffold
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_image
import dvnkdnd.composeapp.generated.resources.navigate_back
import dvnkdnd.composeapp.generated.resources.no_characters_yet
import dvnkdnd.composeapp.generated.resources.select_character_for_info
import dvnkdnd.composeapp.generated.resources.sentiment_dissatisfied
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CharactersListScreen(
    onNewCharacter: () -> Unit,
    navigateToCharacter: (CharacterMin) -> Unit,
    viewModel: CharactersListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            ErrorCard(
                text = stringResource(it.message),
                exception = it.exception,
                onRefresh = viewModel::loadCharacters
            )
        }
        uiState.characters.isEmpty() ->
            FABScaffold(
                onClick = onNewCharacter
            ) {
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
        else -> {
            val navigator = rememberListDetailPaneScaffoldNavigator()
            val coroutineScope = rememberCoroutineScope()
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                scaffoldState = navigator.scaffoldState,
                listPane = {
                    AnimatedPane {
                        FABScaffold(
                            onClick = onNewCharacter
                        ) {
                            CharactersList(
                                modifier = Modifier.fillMaxSize(),
                                items = uiState.characters,
                                onClick = { character ->
                                    viewModel.selectCharacter(character)
                                    coroutineScope.launch {
                                        navigator.navigateTo(
                                            ListDetailPaneScaffoldRole.Detail
                                        )
                                    }
                                }
                            )
                        }
                    }
                },
                detailPane = {
                    AnimatedPane {
                        if (uiState.currentCharacter != null)
                            CharacterInfo(
                                uiState.currentCharacter!!,
                                {
                                    coroutineScope.launch {
                                        navigator.navigateBack()
                                    }
                                },
                                viewModel
                            )
                        else
                            FullScreenCard {
                                Text(stringResource(Res.string.select_character_for_info))
                            }
                    }
                }
            )
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterInfo(
    character: CharacterMin,
    navigateBack: () -> Unit,
    viewModel: CharactersListViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.name) },
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) {

    }
}
