package com.davanok.dvnkdnd.ui.pages.charactersList.characterShortInfo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.ui.components.DescriptionIconButton
import com.davanok.dvnkdnd.ui.components.UiStateHandler
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import dvnkdnd.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterShortInfoScreen(
    navigateToCharacter: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: CharacterShortInfoViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.character?.character?.name ?: stringResource(Res.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    DescriptionIconButton(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = stringResource(Res.string.back),
                        onClick = navigateToCharacter
                    )
                }
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            isLoading = uiState.isLoading,
            error = uiState.error,
            modifier = Modifier.padding(paddingValues)
        ) {
            uiState.character?.let { character ->
                Content(character = character)
            }
        }
    }
}

@Composable
private fun Content(
    character: CharacterFull,
    modifier: Modifier = Modifier
) {
    Text(modifier = modifier, text = character.character.name)
}