package com.davanok.dvnkdnd.ui.pages.charactersList.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.ui.model.UiError
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_characters_error
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.compose.resources.getString

class CharactersListViewModel(
    repository: CharactersRepository
) : ViewModel() {
    private val _characters = repository.getCharactersWithImagesListFlow()

    val uiState: StateFlow<CharactersListUiState> = _characters.map { result ->
        result.fold(
            onSuccess = {
                CharactersListUiState(characters = it)
            },
            onFailure = {
                CharactersListUiState(
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
}

data class CharactersListUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val characters: List<CharacterMin> = emptyList(),
)