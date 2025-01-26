package com.davanok.dvnkdnd.ui.pages.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.repositories.CharactersListRepository
import com.davanok.dvnkdnd.data.model.util.Async
import com.davanok.dvnkdnd.data.model.util.WhileUiSubscribed
import com.davanok.dvnkdnd.database.entities.character.CharacterMin
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_characters_error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

data class CharactersListUiState(
    val characters: List<CharacterMin> = emptyList(),
    val isLoading: Boolean = false,
    val message: StringResource? = null,
    val currentCharacter: CharacterMin? = null
)

class CharactersListViewModel(
    repository: CharactersListRepository
) : ViewModel() {

    private val _currentCharacter = MutableStateFlow<CharacterMin?>(null)

    private val _charactersFlow: Flow<Async<List<CharacterMin>>> = repository
        .getCharactersFlow()
        .map { Async.Success(it) }
        .catch<Async<List<CharacterMin>>> { emit(Async.Error(Res.string.loading_characters_error)) }

    val uiState: StateFlow<CharactersListUiState> = combine(
        _charactersFlow, _currentCharacter
    ) { characters, currentCharacter ->
        when (characters) {
            is Async.Loading -> CharactersListUiState(isLoading = true)
            is Async.Error -> CharactersListUiState(message = characters.errorMessage)
            is Async.Success -> CharactersListUiState(
                characters = characters.data,
                currentCharacter = currentCharacter
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CharactersListUiState(isLoading = true)
    )

    fun selectCharacter(character: CharacterMin) = viewModelScope.launch {
        _currentCharacter.value = character
    }
}