package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.manual_stats_option
import dvnkdnd.composeapp.generated.resources.modifier_selection_invalid
import dvnkdnd.composeapp.generated.resources.point_buy_stats_balance_invalid
import dvnkdnd.composeapp.generated.resources.point_buy_stats_option
import dvnkdnd.composeapp.generated.resources.saving_data_error
import dvnkdnd.composeapp.generated.resources.standard_array_stats_option
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.Uuid

class NewCharacterStatsViewModel(
    private val newCharacterViewModel: NewCharacterViewModel,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterStatsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterStatsUiState> = _uiState

    private var modifierInfo: Map<Uuid, Pair<Int, Set<Uuid>>> = emptyMap()

    fun removeWarning() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun selectCreationOption(option: StatsCreationOptions) {
        _uiState.value = _uiState.value.copy(selectedCreationOptions = option)
    }

    fun loadCharacterWithAllModifiers() {
        val character = newCharacterViewModel.getCharacterWithAllModifiers()

        val allCharacterEntities = (character.classes + listOfNotNull(
            character.race,
            character.subRace,
            character.background,
            character.subBackground
        )).fastFilter { it.modifiers.isNotEmpty() }

        modifierInfo = allCharacterEntities
            .fastFlatMap { entity ->
                val limit = entity.selectionLimit ?: 0
                entity.modifiers.map { mod ->
                    mod.id to (limit to entity.modifiers.map { it.id }.toSet())
                }
            }.toMap()

        _uiState.value = _uiState.value.copy(
            modifiers = character.characterStats ?: DnDModifiersGroup.Default,
            selectedModifiersBonuses = character.selectedModifierBonuses.toSet(),
            allEntitiesWithModifiers = allCharacterEntities,

            isLoading = false
        )
    }

    fun setModifiers(modifiers: DnDModifiersGroup) {
        _uiState.value = _uiState.value.copy(modifiers = modifiers)
    }

    fun selectModifier(modifier: DnDModifierBonus) {
        val current = _uiState.value.selectedModifiersBonuses.toMutableSet()
        val (limit, group) = modifierInfo[modifier.id] ?: (0 to emptySet())

        if (!current.remove(modifier.id)) {
            val alreadySelectedInGroup = group.count { it in current }
            if (alreadySelectedInGroup < limit) {
                current.add(modifier.id)
            }
        }
        _uiState.value = _uiState.value.copy(selectedModifiersBonuses = current)
    }

    private fun checkSelectedModifierBonuses(): Boolean {
        val state = _uiState.value
        if (state.selectedCreationOptions == StatsCreationOptions.POINT_BUY) {
            val modifiersSum =
                calculateBuyingModifiersSum(
                    state
                        .modifiers
                        .toModifiersList()
                        .fastMap { it.modifier }
                )
            val balance = DnDConstants.BUYING_BALANCE - modifiersSum
            if (balance != 0) {
                _uiState.value = state.copy(
                    error = UiError.Warning(Res.string.point_buy_stats_balance_invalid)
                )
                return false
            }
        }

        val selected = state.selectedModifiersBonuses

        val limitsByGroup: Map<Set<Uuid>, Int> = modifierInfo
            .values
            .groupBy(
                keySelector = { it.second },
                valueTransform = { it.first }
            )
            .mapValues { (_, limits) ->
                limits.firstOrNull() ?: 0
            }

        for ((groupSet, limit) in limitsByGroup) {
            val countInGroup = selected.count { it in groupSet }
            if (countInGroup != limit) {
                _uiState.value = state.copy(
                    error = UiError.Warning(Res.string.modifier_selection_invalid)
                )
                return false
            }
        }
        return true
    }

    fun commit(onSuccess: () -> Unit) {
        if (!checkSelectedModifierBonuses()) return
        val state = _uiState.value
        newCharacterViewModel.setCharacterModifiers(
            stats = state.modifiers,
            selectedBonuses = state.selectedModifiersBonuses.toList()
        ).onFailure {
            _uiState.value = state.copy(
                error = UiError.Critical(Res.string.saving_data_error, it)
            )
        }.onSuccess {
            onSuccess()
        }
    }

    init {
        loadCharacterWithAllModifiers()
    }
}

enum class StatsCreationOptions(val title: StringResource) {
    POINT_BUY(Res.string.point_buy_stats_option),
    STANDARD_ARRAY(Res.string.standard_array_stats_option),
    MANUAL(Res.string.manual_stats_option)
}

data class NewCharacterStatsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val selectedCreationOptions: StatsCreationOptions = StatsCreationOptions.POINT_BUY,

    val modifiers: DnDModifiersGroup = DnDModifiersGroup.Default,
    val selectedModifiersBonuses: Set<Uuid> = emptySet(),
    val allEntitiesWithModifiers: List<DnDEntityWithModifiers> = emptyList()
)
