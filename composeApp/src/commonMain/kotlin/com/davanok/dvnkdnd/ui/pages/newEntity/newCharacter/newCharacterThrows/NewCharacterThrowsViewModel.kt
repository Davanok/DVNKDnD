package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrows

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.map
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifierExtendedInfo
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
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

class NewCharacterThrowsViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCharacterThrowsUiState())
    val uiState: StateFlow<NewCharacterThrowsUiState> = _uiState

    fun removeError() = _uiState.update { it.copy(error = null) }

    // backing caches populated on load

    private var attributeModifiers: AttributesGroup = AttributesGroup.Default
        .map(::calculateModifier)

    private var modifierGroups: List<ModifiersGroup> = emptyList()
    private var groupIdToEntityLevel: Map<Uuid, Int> = emptyMap()
    private var groupIdToGroup: Map<Uuid, ModifiersGroup> = emptyMap()
    private var modifierIdToGroupId: Map<Uuid, Uuid> = emptyMap()
    private var modifierIdToModifier: Map<Uuid, DnDModifier> = emptyMap()

    private val selectedModifiers = mutableSetOf<Uuid>()

    fun loadCharacter() = viewModelScope.launch {
        val character = newCharacterViewModel.getCharacterWithAllModifiers()

        // keep selected modifiers set in sync
        selectedModifiers.clear()
        selectedModifiers.addAll(character.selectedModifiers)

        modifierGroups = character.entities
            .fastFlatMap { it.modifiersGroups }
            .fastFilter { it.target == DnDModifierTargetType.SAVING_THROW || it.target == DnDModifierTargetType.SKILL }
            .sortedBy { it.priority }

        groupIdToGroup = modifierGroups.associateBy { it.id }
        modifierIdToGroupId = modifierGroups
            .fastFlatMap { g -> g.modifiers.map { it.id to g.id } }
            .toMap()
        modifierIdToModifier = modifierGroups
            .fastFlatMap { g -> g.modifiers.map { it.id to it } }
            .toMap()

        attributeModifiers = character.attributes.map(::calculateModifier)

        // update UI state once
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                character = character.character,
                proficiencyBonus = proficiencyBonusByLevel(character.character.level),
                attributes = character.attributes,
                savingThrows = getModifiersInfo(DnDModifierTargetType.SAVING_THROW) { attributeModifiers[it] },
                skills = getModifiersInfo(DnDModifierTargetType.SKILL) { attributeModifiers[it.attribute] }
            )
        }
    }

    private fun resolveValueSource(group: ModifiersGroup): Double {
        val stateSnapshot = uiState.value

        val value = when (group.valueSource) {
            DnDModifierValueSource.CONSTANT -> 0
            DnDModifierValueSource.CHARACTER_LEVEL -> stateSnapshot.characterLevel
            DnDModifierValueSource.ENTITY_LEVEL -> groupIdToEntityLevel[group.id] ?: 0
            DnDModifierValueSource.PROFICIENCY_BONUS -> stateSnapshot.proficiencyBonus

            DnDModifierValueSource.ATTRIBUTE -> group.valueSourceTarget
                ?.let { enumValueOfOrNull<Attributes>(it) }
                ?.let { stateSnapshot.attributes[it] } ?: 0

            DnDModifierValueSource.ATTRIBUTE_MODIFIER -> group.valueSourceTarget
                ?.let { enumValueOfOrNull<Attributes>(it) }
                ?.let { stateSnapshot.attributes[it] }
                ?.let { calculateModifier(it) } ?: 0

            DnDModifierValueSource.SAVING_THROW -> group.valueSourceTarget
                ?.let { enumValueOfOrNull<Attributes>(it) }
                ?.let { stateSnapshot.savingThrows[it]?.second } ?: 0

            DnDModifierValueSource.SKILL -> group.valueSourceTarget
                ?.let { enumValueOfOrNull<Skills>(it) }
                ?.let { stateSnapshot.skills[it]?.second } ?: 0
        } + group.value

        return value
    }

    private fun buildExtInfo(
        modifier: DnDModifier,
        group: ModifiersGroup,
        selectable: Boolean,
        selected: Boolean
    ) = ModifierExtendedInfo(
        id = modifier.id,
        isCustom = false,
        groupId = group.id,
        name = group.name,
        target = modifier.target,
        operation = group.operation,
        valueSource = group.valueSource,
        value = group.value,
        state = UiSelectableState(selectable = selectable, selected = selected),
        resolvedValue = resolveValueSource(group),
        priority = group.priority,
        targetGlobal = group.target,
        valueSourceTarget = group.valueSourceTarget,
        clampMin = group.clampMin,
        clampMax = group.clampMax,
        minBaseValue = group.minBaseValue,
        maxBaseValue = group.maxBaseValue
    )

    // toggle selection; returns early if not selectable
    private fun toggleSelectionFor(modifierId: Uuid, target: DnDModifierTargetType) {
        if (selectedModifiers.remove(modifierId)) {
            updateUiFor(target); return
        }

        if (!checkIsModifierSelectable(modifierId)) return

        selectedModifiers.add(modifierId)
        updateUiFor(target)
    }

    fun selectSavingThrow(modifierId: Uuid) = toggleSelectionFor(modifierId, DnDModifierTargetType.SAVING_THROW)
    fun selectSkill(modifierId: Uuid) = toggleSelectionFor(modifierId, DnDModifierTargetType.SKILL)

    private fun updateUiFor(target: DnDModifierTargetType) {
        _uiState.update { state ->
            when (target) {
                DnDModifierTargetType.SAVING_THROW -> state.copy(
                    savingThrows = getModifiersInfo(DnDModifierTargetType.SAVING_THROW) { attribute -> attributeModifiers[attribute] }
                )

                DnDModifierTargetType.SKILL -> state.copy(
                    skills = getModifiersInfo(DnDModifierTargetType.SKILL) { skill -> attributeModifiers[skill.attribute] }
                )

                else -> state
            }
        }
    }

    // Generic function to build modifiers list + computed values for a target
    private inline fun <reified E : Enum<E>> getModifiersInfo(
        target: DnDModifierTargetType,
        baseValueGetter: (E) -> Int
    ): Map<E, Pair<List<ModifierExtendedInfo>, Int>> {
        // collect ModifierExtendedInfo grouped by target name
        val raw = mutableMapOf<String?, MutableList<ModifierExtendedInfo>>()

        modifierGroups.fastForEach { group ->
            if (group.target != target) return@fastForEach

            val selectedInGroup = group.modifiers.count { it.id in selectedModifiers }
            val groupAllowsExtra = selectedInGroup < group.selectionLimit

            group.modifiers.fastForEach { modifier ->
                val selected = modifier.id in selectedModifiers
                val selectable = modifier.selectable && (selected || groupAllowsExtra)

                val ext = buildExtInfo(modifier, group, selectable, selected)
                raw.getOrPut(modifier.target, ::mutableListOf).add(ext)
            }
        }

        // convert keys that match enum names to the enum
        val modifiersByEnum = mutableMapOf<E, List<ModifierExtendedInfo>>()
        raw.forEach { (k, v) ->
            k
                ?.let { enumValueOfOrNull<E>(it) }
                ?.let { modifiersByEnum[it] = v }
        }
        // For each enum entry compute the final modifier value using calculateModifierSum.
        val groupsForTarget = modifierGroups.fastFilter { it.target == target }
        return modifiersByEnum.mapValues { (enumKey, extList) ->
            var result = baseValueGetter(enumKey)
            groupsForTarget
                .sortedBy { it.priority }
                .forEach { group ->
                    // compute group-level value once (nullable => use modifier.value when null)
                    val value = resolveValueSource(group)

                    group.modifiers.forEach modifier@{ modifier ->
                        if (modifier.target != enumKey.name || modifier.id !in selectedModifiers) return@modifier

                        // skip modifier if current base (result) not in group's base range
                        if (group.minBaseValue?.let { result < it } == true) return@modifier
                        if (group.maxBaseValue?.let { result > it } == true) return@modifier

                        // apply operation and clamp to group's limits
                        result = applyOperation(result, value, group.operation)
                            .coerceIn(group.clampMin, group.clampMax)
                    }
                }

            extList to result
        }
    }

    private fun checkIsModifierSelectable(modifierId: Uuid): Boolean {
        val modifier = modifierIdToModifier[modifierId] ?: return false
        if (!modifier.selectable) return false

        val groupId = modifierIdToGroupId[modifierId] ?: return false
        val group = groupIdToGroup[groupId] ?: return false

        val alreadySelected = group.modifiers.count { it.id in selectedModifiers }
        return alreadySelected < group.selectionLimit
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        val isSavingThrowsValid = modifierGroups
            .fastFilter { it.target == DnDModifierTargetType.SAVING_THROW }
            .fastAll { group -> group.modifiers.count { it.id in selectedModifiers } >= group.selectionLimit }

        val isSkillsValid = modifierGroups
            .fastFilter { it.target == DnDModifierTargetType.SKILL }
            .fastAll { group -> group.modifiers.count { it.id in selectedModifiers } >= group.selectionLimit }

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
}

@Immutable
data class NewCharacterThrowsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,

    val character: CharacterBase? = null,
    val characterLevel: Int = 1,
    val proficiencyBonus: Int = proficiencyBonusByLevel(characterLevel),
    val attributes: AttributesGroup = AttributesGroup.Default,

    val savingThrows: Map<Attributes, Pair<List<ModifierExtendedInfo>, Int>> = emptyMap(),// SavingThrows to (modifiers to result value)
    val skills: Map<Skills, Pair<List<ModifierExtendedInfo>, Int>> = emptyMap() // Skill to (modifiers to result value)
)
