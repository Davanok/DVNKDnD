package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_characters_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharactersListViewModel(
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharactersListUiState(isLoading = true))
    val uiState: StateFlow<CharactersListUiState> = _uiState


    fun loadCharacters() = viewModelScope.launch {
        repository.getCharactersMinList().onFailure { thr ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = UiError.Critical(
                        Res.string.loading_characters_error,
                        thr
                    )
                )
            }
        }.onSuccess { characters ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    characters = characters
                )
            }
        }
    }

    fun selectCharacter(character: CharacterMin) = _uiState.update {
        it.copy(currentCharacter = character)
    }

    init {
        loadCharacters()
    }
}

data class CharactersListUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val characters: List<CharacterMin> = emptyList(),
    val currentCharacter: CharacterMin? = null
)