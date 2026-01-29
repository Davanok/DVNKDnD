package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.dnd.DnDConstants
import com.davanok.dvnkdnd.domain.dnd.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.ValueModifiersGroupWithResolvedValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.modifiers
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.ui.model.UiError
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid
class NewCharacterAttributesViewModel(
    private val newCharacterViewModel: NewCharacterViewModel,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCharacterStatsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterStatsUiState> = _uiState.asStateFlow()

    // Cache for O(1) lookups during selection.
    // Maps a Modifier ID to its specific group constraints.
    private var modifierRules: Map<Uuid, ModifierGroupRules> = emptyMap()

    init {
        loadCharacterWithAllModifiers()
    }

    fun removeWarning() {
        _uiState.update { it.copy(error = null) }
    }

    fun selectAttributeSelectorType(option: AttributesSelectorType) {
        _uiState.update { it.copy(attributesSelectorType = option) }
    }

    fun setModifiers(modifiers: AttributesGroup) {
        _uiState.update { it.copy(modifiers = modifiers) }
    }

    fun loadCharacterWithAllModifiers() {
        val character = newCharacterViewModel.getCharacterWithAllModifiers()

        // Pre-calculate rules for fast lookups during user interaction (selectModifier)
        modifierRules = buildMap {
            character.modifierGroups
                .filter { group -> group.modifiers.all { it.modifier.targetScope == ModifierValueTarget.ATTRIBUTE } }
                .forEach { group ->
                    val groupIds = group.modifiers.map { it.modifier.id }.toSet()
                    val rules = ModifierGroupRules(limit = group.selectionLimit, groupIds = groupIds)

                    groupIds.forEach { modifierId ->
                        put(modifierId, rules)
                    }
                }
        }

        _uiState.update {
            it.copy(
                character = character.character,
                modifiers = character.attributes,
                selectedAttributesBonuses = character.selectedModifiers,
                allModifiersGroups = character.modifierGroups,
                isLoading = false
            )
        }
    }

    fun selectModifier(modifier: DnDModifier) {
        _uiState.update { state ->
            val rules = modifierRules[modifier.id] ?: return@update state
            val currentSelected = state.selectedAttributesBonuses.toMutableSet()

            if (modifier.id in currentSelected) {
                currentSelected.remove(modifier.id)
            } else {
                // Check if adding this would exceed the group limit
                val selectedCountInGroup = rules.groupIds.count { it in currentSelected }
                if (selectedCountInGroup < rules.limit) {
                    currentSelected.add(modifier.id)
                }
            }
            state.copy(selectedAttributesBonuses = currentSelected)
        }
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        if (!validateSelection()) return@launch

        val state = _uiState.value

        newCharacterViewModel.setCharacterModifiers(
            attributes = state.modifiers,
            selectedModifiers = state.selectedAttributesBonuses.toList()
        ).onSuccess {
            onSuccess()
        }.onFailure { thr ->
            _uiState.update {
                it.copy(
                    error = UiError.Critical(
                        message = getString(Res.string.saving_data_error),
                        exception = thr
                    )
                )
            }
        }
    }

    private suspend fun validateSelection(): Boolean {
        val state = _uiState.value

        // 1. Validate Point Buy Balance
        if (state.attributesSelectorType == AttributesSelectorType.POINT_BUY) {
            val modifiersSum = calculateBuyingModifiersSum(state.modifiers.modifiers())
            val balance = DnDConstants.BUYING_BALANCE - modifiersSum

            if (balance != 0) {
                emitWarning(Res.string.point_buy_stats_balance_invalid)
                return false
            }
        }

        // 2. Validate Modifier Group Limits
        // We iterate the source of truth (allModifiersGroups) directly rather than reconstructing it.
        val invalidGroupExists = state.allModifiersGroups
            .filter { group -> group.modifiers.all { it.modifier.targetScope == ModifierValueTarget.ATTRIBUTE } }
            .any { group ->
                val groupIds = group.modifiers.map { it.modifier.id }.toSet()
                val selectedInGroup = state.selectedAttributesBonuses.count { it in groupIds }
                selectedInGroup != group.selectionLimit
            }

        if (invalidGroupExists) {
            emitWarning(Res.string.modifier_selection_invalid)
            return false
        }

        return true
    }

    private suspend fun emitWarning(messageRes: StringResource) {
        _uiState.update {
            it.copy(error = UiError.Warning(message = getString(messageRes)))
        }
    }

    // Helper class to make Map types readable
    private data class ModifierGroupRules(
        val limit: Int,
        val groupIds: Set<Uuid>
    )
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
    val modifiers: AttributesGroup = AttributesGroup.Default,
    val selectedAttributesBonuses: Set<Uuid> = emptySet(),
    // Keeping this list allows us to validate logic without auxiliary maps
    val allModifiersGroups: List<ValueModifiersGroupWithResolvedValues> = emptyList()
)