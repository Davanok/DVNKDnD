package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun NewCharacterSkillsScreen(
    characterId: Uuid,
    onBack: (characterId: Uuid) -> Unit,
    onContinue: (characterId: Uuid) -> Unit,
    viewModel: NewCharacterSkillsViewModel = koinViewModel()
) {

}