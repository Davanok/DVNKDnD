package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.withAppliedModifiers
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_character_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

class CharacterFullViewModel(
    private val characterId: Uuid,
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterFullUiState(isLoading = true))
    val uiState: StateFlow<CharacterFullUiState> = _uiState

    fun loadCharacter() = viewModelScope.launch {
        repository.getFullCharacter(characterId)
            .onFailure { thr ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = UiError.Critical(
                            getString(Res.string.loading_character_error),
                            thr
                        )
                    )
                }
            }.onSuccess { character ->
                val characterApplied = character?.withAppliedModifiers()
                _uiState.update { it.copy(isLoading = false, character = characterApplied) }
            }
    }

    fun updateHealth(health: DnDCharacterHealth) = viewModelScope.launch {

    }

    init {
        loadCharacter()
    }
}

data class CharacterFullUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterFull? = null,
    val dialogToDisplay: CharacterDialog = CharacterDialog.NONE
) {
    enum class CharacterDialog {
        HEALTH, NONE
    }
}