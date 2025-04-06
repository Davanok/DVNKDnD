package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.manual_stats_option
import dvnkdnd.composeapp.generated.resources.point_buy_stats_option
import dvnkdnd.composeapp.generated.resources.standard_array_stats_option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

class NewCharacterStatsViewModel(
    private val repository: NewCharacterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterStatsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterStatsUiState> = _uiState

    fun selectCreationOption(option: StatsCreationOptions) {
        _uiState.value = _uiState.value.copy(selectedCreationOptions = option)
    }
}
enum class StatsCreationOptions(val title: StringResource) {
    POINT_BUY(Res.string.point_buy_stats_option),
    STANDARD_ARRAY(Res.string.standard_array_stats_option),
    MANUAL(Res.string.manual_stats_option)
}
data class NewCharacterStatsUiState(
    val isLoading: Boolean = false,
    val selectedCreationOptions: StatsCreationOptions = StatsCreationOptions.POINT_BUY
)
