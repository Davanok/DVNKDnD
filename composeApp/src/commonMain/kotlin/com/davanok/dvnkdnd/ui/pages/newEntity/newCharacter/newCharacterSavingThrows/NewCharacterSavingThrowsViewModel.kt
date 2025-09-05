package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.ui.UiSelectableState
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.not_all_available_saving_throws_selected
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class NewCharacterSavingThrowsViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterSavingThrowsUiState())
    val uiState: StateFlow<NewCharacterSavingThrowsUiState> = _uiState

    fun removeError() = _uiState.update { it.copy(error = null) }
    private lateinit var savingThrowsState: SavingThrowsTableState

    fun loadCharacterWithSavingThrows() {
        val character = newCharacterViewModel.getCharacterWithAllSavingThrows()
        savingThrowsState = SavingThrowsTableState(
            character.entities,
            character.selectedSavingThrows.toSet()
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                character = newCharacterViewModel.getCharacterShortInfo(),
                proficiencyBonus = character.proficiencyBonus,
                selectionLimit = character.entities.sumOf { e -> e.selectionLimit },
                stats = character.stats,
                savingThrows = savingThrowsState.getDisplayItems()
            )
        }
    }

    fun selectSavingThrow(stat: Stats) {
        if (savingThrowsState.select(stat)) _uiState.update {
            it.copy(savingThrows = savingThrowsState.getDisplayItems())
        }
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        if (savingThrowsState.validateSelectedSavingThrows())
            newCharacterViewModel.setCharacterSavingThrows(savingThrowsState.getSelectedSavingThrows().toList())
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
        else
            _uiState.update {
                it.copy(
                    error = UiError.Warning(
                        message = getString(Res.string.not_all_available_saving_throws_selected)
                    )
                )
            }
    }

    init {
        loadCharacterWithSavingThrows()
    }
}

data class NewCharacterSavingThrowsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterShortInfo = CharacterShortInfo(),
    val proficiencyBonus: Int = 0,
    val selectionLimit: Int = 0,
    val stats: DnDModifiersGroup = DnDModifiersGroup.Default,
    val savingThrows: Map<Stats, UiSelectableState> = emptyMap()
)