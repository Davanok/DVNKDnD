package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth

import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NewCharacterHealthViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterHealthUiState())
    val uiState: StateFlow<NewCharacterHealthUiState> = _uiState

    fun removeError() = _uiState.update { it.copy(error = null) }

    fun commit(onSuccess: () -> Unit) {

    }
}

data class NewCharacterHealthUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    // TODO
)