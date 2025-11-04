package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterNote
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.util.withAppliedModifiers
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import dvnkdnd.composeapp.generated.resources.character_full_attacks_page_title
import dvnkdnd.composeapp.generated.resources.character_full_attributes_page_title
import dvnkdnd.composeapp.generated.resources.character_full_items_page_title
import dvnkdnd.composeapp.generated.resources.character_full_notes_page_title
import dvnkdnd.composeapp.generated.resources.character_full_spells_page_title
import dvnkdnd.composeapp.generated.resources.character_health_dialog_title
import dvnkdnd.composeapp.generated.resources.loading_character_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

class CharacterFullViewModel(
    private val characterId: Uuid,
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterFullUiState(isLoading = true))
    private val _character = repository.getFullCharacterFlow(characterId)

    val uiState = combine(_uiState, _character) { uiState, characterResult ->
        characterResult.fold(
            onSuccess = { character ->
                uiState.copy(
                    character = character.withAppliedModifiers(),
                    isLoading = false
                )
            },
            onFailure = { thr ->
                uiState.copy(
                    error = UiError.Critical(
                        getString(Res.string.loading_character_error),
                        thr
                    ),
                    isLoading = false
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharacterFullUiState(isLoading = true)
    )

    fun updateHealth(health: DnDCharacterHealth) = viewModelScope.launch {
        repository.setCharacterHealth(characterId, health)
    }
    fun updateOrNewNote(note: CharacterNote) = viewModelScope.launch {
        repository.setCharacterNote(characterId, note)
    }
    fun deleteNote(note: CharacterNote) = viewModelScope.launch {
        repository.deleteCharacterNote(note.id)
    }
}

data class CharacterFullUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterFull? = null,
) {
    enum class Page(val stringRes: StringResource) {
        ATTRIBUTES(Res.string.character_full_attributes_page_title),
        ATTACKS(Res.string.character_full_attacks_page_title),
        ITEMS(Res.string.character_full_items_page_title),
        SPELLS(Res.string.character_full_spells_page_title),
        NOTES(Res.string.character_full_notes_page_title),
    }
    enum class Dialog(val titleStringRes: StringResource) {
        HEALTH(Res.string.character_health_dialog_title),
        NONE(Res.string.app_name)
    }
}