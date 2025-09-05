package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.health_cant_be_less_than_zero
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class NewCharacterHealthViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterHealthUiState())
    val uiState: StateFlow<NewCharacterHealthUiState> = _uiState

    fun removeError() = _uiState.update { it.copy(error = null) }

    fun loadCharacter() {
        val character = newCharacterViewModel.getCharacterWithHealth()
        _uiState.update {
            it.copy(
                isLoading = false,
                healthDice = character.healthDice,
                characterConstitution = character.constitution,
                baseHealth = character.baseHealth
            )
        }
    }

    fun setHealth(health: Int) = _uiState.update { it.copy(baseHealth = health) }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        val baseHealth = uiState.value.baseHealth
        if (baseHealth < 1) _uiState.update {
            it.copy(
                error = UiError.Warning(
                    getString(Res.string.health_cant_be_less_than_zero)
                )
            )
        }
        else newCharacterViewModel.setCharacterHealth(baseHealth)
            .onFailure { thr ->
                _uiState.update {
                    it.copy(
                        error = UiError.Critical(
                            message = getString(Res.string.saving_data_error),
                            exception = thr
                        )
                    )
                }
            }.onSuccess {
                onSuccess()
            }
    }

    init {
        loadCharacter()
    }
}

data class NewCharacterHealthUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val healthDice: Dices? = null,
    val characterConstitution: Int = 10,
    val baseHealth: Int = 1,
)