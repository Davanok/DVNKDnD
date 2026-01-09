package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.ui.model.UiError
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_character_error
import dvnkdnd.composeapp.generated.resources.loading_characters_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

class CharactersListViewModel(
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharactersListUiState(isLoading = true))
    private val _characters = repository.getCharactersWithImagesListFlow()

    val uiState: StateFlow<CharactersListUiState> = combine(
        _uiState,
        _characters
    ) { uiState, characters ->
        characters.fold(
            onSuccess = {
                uiState.copy(characters = it, isLoading = false)
            },
            onFailure = {
                uiState.copy(
                    isLoading = false,
                    error = UiError.Critical(
                        message = getString(Res.string.loading_characters_error),
                        exception = it
                    )
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharactersListUiState(isLoading = true)
    )

    private val charactersCache = mutableMapOf<Uuid, CharacterFull>()

    fun selectCharacter(character: CharacterMin) = viewModelScope.launch {
        var characterFull = charactersCache[character.id]

        if (characterFull == null) {
            _uiState.update { it.copy(isCurrentCharacterLoading = true) }

            repository.getFullCharacter(character.id).onFailure { thr ->
                _uiState.update {
                    it.copy(
                        error = UiError.Critical(
                            message = getString(Res.string.loading_character_error),
                            exception = thr
                        )
                    )
                }
            }.onSuccess {
                charactersCache[character.id] = it
                characterFull = it
            }
        }

        _uiState.update {
            it.copy(
                isCurrentCharacterLoading = false,
                currentCharacter = characterFull
            )
        }
    }
}

data class CharactersListUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val characters: List<CharacterMin> = emptyList(),
    val currentCharacter: CharacterFull? = null,
    val isCurrentCharacterLoading: Boolean = false
)