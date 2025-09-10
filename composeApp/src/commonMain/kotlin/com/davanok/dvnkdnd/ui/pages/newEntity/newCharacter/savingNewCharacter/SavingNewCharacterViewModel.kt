package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

class SavingNewCharacterViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(SavingNewCharacterUiState(isLoading = true))
    val uiState: StateFlow<SavingNewCharacterUiState> = _uiState

    fun savaCharacter() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        newCharacterViewModel.saveCharacter().onSuccess { characterId ->
            _uiState.update { it.copy(isLoading = false, characterId = characterId) }
        }.onFailure { thr ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = UiError.Critical(
                        message = getString(Res.string.saving_data_error),
                        exception = thr
                    )
                )
            }
        }
    }

    init {
        savaCharacter()
    }
}

data class SavingNewCharacterUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val characterId: Uuid? = null
)