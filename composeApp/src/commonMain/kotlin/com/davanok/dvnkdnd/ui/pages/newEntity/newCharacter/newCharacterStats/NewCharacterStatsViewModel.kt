package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.manual_stats_option
import dvnkdnd.composeapp.generated.resources.point_buy_stats_option
import dvnkdnd.composeapp.generated.resources.standard_array_stats_option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.Uuid

class NewCharacterStatsViewModel(
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterStatsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterStatsUiState> = _uiState

    fun selectCreationOption(option: StatsCreationOptions) {
        _uiState.value = _uiState.value.copy(selectedCreationOptions = option)
    }

    fun loadCharacterWithModifiers(characterId: Uuid) = viewModelScope.launch {
        var character = repository.getCharacterWithModifiers(characterId)
        if (character.characterStats == null)
            character = character.copy(
                characterStats = DnDModifiersGroup.Default
            )
        _uiState.value = _uiState.value.copy(
            character = character,
            isLoading = false
        )
    }
    fun setModifiers(modifiers: DnDModifiersGroup) {
        _uiState.value = _uiState.value.copy(modifiers = modifiers)
    }
    fun createCharacter(onSuccess: (characterId: Uuid) -> Unit) = viewModelScope.launch {

    }
}
enum class StatsCreationOptions(val title: StringResource) {
    POINT_BUY(Res.string.point_buy_stats_option),
    STANDARD_ARRAY(Res.string.standard_array_stats_option),
    MANUAL(Res.string.manual_stats_option)
}
data class NewCharacterStatsUiState(
    val isLoading: Boolean = false,
    val selectedCreationOptions: StatsCreationOptions = StatsCreationOptions.POINT_BUY,
    val character: CharacterWithModifiers? = null,
    val modifiers: DnDModifiersGroup = character?.characterStats?: DnDModifiersGroup.Default
)
