package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CharactersListUiState(
    val characters: List<CharacterMin> = emptyList(),
    val isLoading: Boolean = false,
    val currentCharacter: CharacterMin? = null
)

class CharactersListViewModel(
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharactersListUiState(isLoading = true))
    val uiState: StateFlow<CharactersListUiState> = _uiState


    fun loadCharacters() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            characters = repository.getCharactersMinList()
        )
    }

    fun selectCharacter(character: CharacterMin) {
        _uiState.value = _uiState.value.copy(
            currentCharacter = character
        )
    }

    init {
        loadCharacters()
    }
}