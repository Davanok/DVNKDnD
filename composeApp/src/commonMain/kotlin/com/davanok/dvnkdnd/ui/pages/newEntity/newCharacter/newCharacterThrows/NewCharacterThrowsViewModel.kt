package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.ValueModifiersGroupWithResolvedValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroupInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.map
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.not_all_available_saving_throws_selected
import dvnkdnd.composeapp.generated.resources.not_all_available_skills_selected
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

@AssistedInject
class NewCharacterThrowsViewModel(
    @Assisted private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCharacterThrowsUiState())
    val uiState: StateFlow<NewCharacterThrowsUiState> = _uiState

    fun removeError() = _uiState.update { it.copy(error = null) }

    // backing caches populated on load

    private var attributeModifiers: AttributesGroup = AttributesGroup.Default
        .map(::calculateModifier)
    var modifierGroups = emptyList<ValueModifiersGroupWithResolvedValues>()

    private val selectedModifiers = mutableSetOf<Uuid>()

    private fun updateModifiersCache(groupWithResolvedValues: List<ValueModifiersGroupWithResolvedValues>) {
        modifierGroups = groupWithResolvedValues
            .filter { group ->
                group.modifiers.all {
                    it.modifier.targetScope == ModifierValueTarget.SAVING_THROW ||
                            it.modifier.targetScope == ModifierValueTarget.SKILL
                }
            }
    }

    fun loadCharacter() = viewModelScope.launch {
        val character = newCharacterViewModel.getCharacterWithAllModifiers()

        // keep selected modifiers set in sync
        selectedModifiers.clear()
        selectedModifiers.addAll(character.selectedModifiers)

        attributeModifiers = character.attributes.map(::calculateModifier)

        updateModifiersCache(character.modifierGroups)

        // update UI state once
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                character = character.character,
                proficiencyBonus = proficiencyBonusByLevel(character.character.level),
                attributes = character.attributes,
                savingThrows = getModifiersInfo(ModifierValueTarget.SAVING_THROW) { attributeModifiers[it] },
                skills = getModifiersInfo(ModifierValueTarget.SKILL) { attributeModifiers[it.attribute] }
            )
        }
    }

    // toggle selection; returns early if not selectable
    private fun toggleSelectionFor(modifierId: Uuid, target: ModifierValueTarget) {
        if (selectedModifiers.remove(modifierId)) {
            updateUiFor(target); return
        }

        if (!checkIsModifierSelectable(modifierId)) return

        selectedModifiers.add(modifierId)
        updateUiFor(target)
    }

    fun selectSavingThrow(modifierId: Uuid) = toggleSelectionFor(modifierId, ModifierValueTarget.SAVING_THROW)
    fun selectSkill(modifierId: Uuid) = toggleSelectionFor(modifierId, ModifierValueTarget.SKILL)

    private fun updateUiFor(target: ModifierValueTarget) {
        _uiState.update { state ->
            when (target) {
                ModifierValueTarget.SAVING_THROW -> state.copy(
                    savingThrows = getModifiersInfo(ModifierValueTarget.SAVING_THROW) { attribute -> attributeModifiers[attribute] }
                )

                ModifierValueTarget.SKILL -> state.copy(
                    skills = getModifiersInfo(ModifierValueTarget.SKILL) { skill -> attributeModifiers[skill.attribute] }
                )

                else -> state
            }
        }
    }

    // Generic function to build modifiers list + computed values for a target
    private inline fun <reified E : Enum<E>> getModifiersInfo(
        target: ModifierValueTarget,
        baseValueGetter: (E) -> Int
    ): Map<E, ModifierResultValue> {
        // 1. Prepare the container for the results using an EnumMap (efficient for Enums)
        // using a generic MutableMap here for compatibility, but ideally use EnumMap<E, ...> if possible.
        val modifiersByEnum = mutableMapOf<E, MutableList<ValueModifierInfo>>()

        // 2. Identify relevant groups once
        val groupsForTarget = modifierGroups.filter { group ->
            group.modifiers.all { it.modifier.targetScope == target }
        }

        // 3. Single Pass: Build UI state and group modifiers by Enum
        groupsForTarget.forEach { group ->
            val groupInfo = ModifiersGroupInfo(
                id = group.id,
                name = group.name,
                description = group.description,
                selectionLimit = group.selectionLimit
            )

            // Calculate group selection state
            val selectedCount = group.modifiers.count { it.modifier.id in selectedModifiers }
            val groupAllowsExtra = selectedCount < group.selectionLimit
            val isGroupSelectable = group.selectionLimit in 1..<group.modifiers.size

            group.modifiers.forEach { (modifier, resolvedValue) ->
                // Resolve String key to Enum immediately. Skip if invalid or null.
                val enumKey = modifier.targetKey?.let { enumValueOfOrNull<E>(it) } ?: return@forEach

                val isSelected = modifier.id in selectedModifiers
                val isSelectable = isGroupSelectable && (isSelected || groupAllowsExtra)

                val info = ValueModifierInfo(
                    isCustom = false,
                    modifier = modifier,
                    group = groupInfo,
                    resolvedValue = resolvedValue,
                    state = UiSelectableState(isSelectable, isSelected)
                )

                modifiersByEnum.getOrPut(enumKey) { mutableListOf() }.add(info)
            }
        }

        // 4. Calculate Final Values
        // We only iterate over the Enums that actually have modifiers (or all Enums if required, see note below)
        return modifiersByEnum.mapValues { (enumKey, infoList) ->
            var result = baseValueGetter(enumKey)

            // Get only the SELECTED modifiers for this specific Enum to calculate the value
            val activeModifiers = infoList
                .filter { it.state.selected } // Only apply selected modifiers
                .sortedBy { it.modifier.priority } // Sort by priority

            activeModifiers.forEach { info ->
                result = applyOperation(result, info.resolvedValue, info.modifier.operation)
            }

            ModifierResultValue(
                modifiers = infoList,
                resultValue = result
            )
        }
    }

    private fun checkIsModifierSelectable(modifierId: Uuid): Boolean {
        val modifierGroup = modifierGroups.firstOrNull { group ->
            group.modifiers.map { it.modifier.id }.contains(modifierId)
        } ?: return false
        val modifierSelectable = modifierGroup.selectionLimit <= 0
        if (!modifierSelectable) return false

        val alreadySelected = modifierGroup.modifiers.count { it.modifier.id in selectedModifiers }
        return alreadySelected < modifierGroup.selectionLimit
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        val isSavingThrowsValid = modifierGroups
            .fastFilter { group -> group.modifiers.all { it.modifier.targetScope == ModifierValueTarget.SAVING_THROW } }
            .fastAll { group -> group.modifiers.count { it.modifier.id in selectedModifiers } >= group.selectionLimit }

        val isSkillsValid = modifierGroups
            .fastFilter { group -> group.modifiers.all { it.modifier.targetScope == ModifierValueTarget.SKILL } }
            .fastAll { group -> group.modifiers.count { it.modifier.id in selectedModifiers } >= group.selectionLimit }

        if (!isSavingThrowsValid) {
            _uiState.update {
                it.copy(error = UiError.Warning(message = getString(Res.string.not_all_available_saving_throws_selected)))
            }
            return@launch
        }

        if (!isSkillsValid) {
            _uiState.update {
                it.copy(error = UiError.Warning(message = getString(Res.string.not_all_available_skills_selected)))
            }
            return@launch
        }

        newCharacterViewModel.setCharacterSelectedModifiers(selectedModifiers)
            .onFailure { thr ->
                _uiState.update { it.copy(error = UiError.Critical(message = getString(Res.string.saving_data_error), exception = thr)) }
            }
            .onSuccess { onSuccess() }
    }

    init {
        loadCharacter()
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted newCharacterViewModel: NewCharacterViewModel): NewCharacterThrowsViewModel
    }
}

data class ModifierResultValue(
    val modifiers: List<ValueModifierInfo>,
    val resultValue: Int
)

@Immutable
data class NewCharacterThrowsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,

    val character: CharacterBase? = null,
    val characterLevel: Int = 1,
    val proficiencyBonus: Int = proficiencyBonusByLevel(characterLevel),
    val attributes: AttributesGroup = AttributesGroup.Default,

    val savingThrows: Map<Attributes, ModifierResultValue> = emptyMap(),// SavingThrows to (modifiers to result value)
    val skills: Map<Skills, ModifierResultValue> = emptyMap() // Skill to (modifiers to result value)
)
