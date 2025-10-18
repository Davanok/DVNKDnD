package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.customToolBar.CollapsingTitle
import com.davanok.dvnkdnd.ui.components.customToolBar.CustomTopAppBar
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterHealthDialog
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterHealthWidget
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.no_such_character_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterFullScreen(
    navigateBack: () -> Unit,
    viewModel: CharacterFullViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            ErrorCard(
                text = it.message,
                exception = it.exception,
                onRefresh = viewModel::loadCharacter,
                onBack = navigateBack
            )
        }
        else -> uiState.character.let { character ->
            if (character == null)
                ErrorCard(
                    text = stringResource(Res.string.no_such_character_error),
                    onRefresh = viewModel::loadCharacter,
                    onBack = navigateBack
                )
            else
                Content(
                    navigateBack = navigateBack,
                    character = character,
                    dialogToDisplay = uiState.dialogToDisplay,
                    updateHealth = viewModel::updateHealth
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    navigateBack: () -> Unit,
    character: CharacterFull,
    dialogToDisplay: CharacterFullUiState.CharacterDialog,
    updateHealth: (DnDCharacterHealth) -> Unit,
) {
    var showHealthDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                collapsingTitle = CollapsingTitle.medium(character.character.name),
                additionalContent = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CharacterHealthWidget(
                            health = character.appliedValues.health,
                            onClick = { showHealthDialog = !showHealthDialog }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

        }
    }

    when (dialogToDisplay) {
        CharacterFullUiState.CharacterDialog.HEALTH -> CharacterHealthDialog(
            onDismissRequest = { showHealthDialog = false },
            baseHealth = character.health,
            updateHealth = updateHealth,
            healthModifiers = character.appliedModifiers
                .getOrElse(DnDModifierTargetType.HEALTH, ::emptyList)
        )
        else -> {  }
    }
}