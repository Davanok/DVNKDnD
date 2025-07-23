package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.ui.components.LoadingCard
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun NewCharacterSkillsScreen(
    characterId: Uuid,
    onBack: (characterId: Uuid) -> Unit,
    onContinue: (characterId: Uuid) -> Unit,
    viewModel: NewCharacterSkillsViewModel = koinViewModel()
) {
    LaunchedEffect(characterId) {
        viewModel.loadCharacterWithSkills(characterId)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        state.isLoading -> LoadingCard()

    }
}