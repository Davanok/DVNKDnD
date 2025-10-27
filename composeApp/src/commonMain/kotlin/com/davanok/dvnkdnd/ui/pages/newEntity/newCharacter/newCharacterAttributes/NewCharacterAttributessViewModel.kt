package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.modifiers
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

class NewCharacterStatsViewModel(
    private val newCharacterViewModel: NewCharacterViewModel,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterStatsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterStatsUiState> = _uiState

    // modifierId to (selection limit, modifierIds in group)
    private var modifierInfo: Map<Uuid, Pair<Int, Set<Uuid>>> = emptyMap()

    fun removeWarning() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun selectAttributeSelectorType(option: AttributesSelectorType) {
        _uiState.value = _uiState.value.copy(attributesSelectorType = option)
    }

    fun loadCharacterWithAllModifiers() {
        val character = newCharacterViewModel.getCharacterWithAllModifiers()

        val attributeModifiersGroups = character.entitiesWithLevel
            .fastFlatMap { it.first.modifiersGroups }
            .fastFilter { it.target == DnDModifierTargetType.ATTRIBUTE }

        modifierInfo = attributeModifiersGroups
            .fastFlatMap { group ->
                group.modifiers.fastMap { mod ->
                    mod.id to (group.selectionLimit to group.modifiers.map { it.id }.toSet())
                }
            }.toMap()

        _uiState.update {
            it.copy(
                character = character.character,
                modifiers = character.attributes,
                selectedAttributesBonuses = character.selectedModifiers.toSet(),
                allModifiersGroups = attributeModifiersGroups,
                isLoading = false
            )
        }
    }

    fun setModifiers(modifiers: DnDAttributesGroup) {
        _uiState.value = _uiState.value.copy(modifiers = modifiers)
    }

    fun selectModifier(modifier: DnDModifier) {
        val current = _uiState.value.selectedAttributesBonuses.toMutableSet()
        val (limit, group) = modifierInfo[modifier.id] ?: (0 to emptySet())

        if (!current.remove(modifier.id)) {
            val alreadySelectedInGroup = group.count { it in current }
            if (alreadySelectedInGroup < limit) {
                current.add(modifier.id)
            }
        }
        _uiState.value = _uiState.value.copy(selectedAttributesBonuses = current)
    }

    private suspend fun checkSelectedModifierBonuses(): Boolean {
        val state = _uiState.value
        if (state.attributesSelectorType == AttributesSelectorType.POINT_BUY) {
            val modifiersSum =
                calculateBuyingModifiersSum(
                    state
                        .modifiers
                        .modifiers()
                )
            val balance = DnDConstants.BUYING_BALANCE - modifiersSum
            if (balance != 0) {
                _uiState.value = state.copy(
                    error = UiError.Warning(
                        message = getString(Res.string.point_buy_stats_balance_invalid)
                    )
                )
                return false
            }
        }

        val selected = state.selectedAttributesBonuses

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
                    error = UiError.Warning(
                        message = getString(Res.string.modifier_selection_invalid)
                    )
                )
                return false
            }
        }
        return true
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        if (checkSelectedModifierBonuses()) {
            val state = _uiState.value
            newCharacterViewModel.setCharacterModifiers(
                attributes = state.modifiers,
                selectedModifiers = state.selectedAttributesBonuses.toList()
            ).onFailure { thr ->
                _uiState.value = state.copy(
                    error = UiError.Critical(
                        message = getString(Res.string.saving_data_error),
                        exception = thr
                    )
                )
            }.onSuccess {
                onSuccess()
            }
        }
    }

    init {
        loadCharacterWithAllModifiers()
    }
}

enum class AttributesSelectorType(val title: StringResource) {
    POINT_BUY(Res.string.point_buy_stats_option),
    STANDARD_ARRAY(Res.string.standard_array_stats_option),
    MANUAL(Res.string.manual_stats_option)
}

data class NewCharacterStatsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val attributesSelectorType: AttributesSelectorType = AttributesSelectorType.POINT_BUY,

    val character: CharacterBase? = null,
    val modifiers: DnDAttributesGroup = DnDAttributesGroup.Default,
    val selectedAttributesBonuses: Set<Uuid> = emptySet(),
    val allModifiersGroups: List<DnDModifiersGroup> = emptyList()
)
