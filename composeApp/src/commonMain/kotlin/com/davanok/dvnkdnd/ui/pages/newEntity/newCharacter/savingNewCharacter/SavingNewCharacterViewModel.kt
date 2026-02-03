package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.savingNewCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

@AssistedInject
class SavingNewCharacterViewModel(
    @Assisted private val newCharacterViewModel: NewCharacterViewModel
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

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted newCharacterViewModel: NewCharacterViewModel): SavingNewCharacterViewModel
    }
}

data class SavingNewCharacterUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val characterId: Uuid? = null
)