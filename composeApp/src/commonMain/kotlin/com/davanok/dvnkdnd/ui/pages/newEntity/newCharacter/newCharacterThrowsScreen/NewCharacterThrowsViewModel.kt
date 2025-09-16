package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrowsScreen

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.calculateModifierSum
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.util.proficiencyBonusByLevel
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
    private var modifierGroups: List<DnDModifiersGroup> = emptyList()
    private var groupIdToGroup: Map<Uuid, DnDModifiersGroup> = emptyMap()
    private var modifierIdToGroupId: Map<Uuid, Uuid> = emptyMap()
    private var modifierIdToModifier: Map<Uuid, DnDModifier> = emptyMap()

    private val selectedModifiers = mutableSetOf<Uuid>()

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        val character = newCharacterViewModel.getCharacterWithAllModifiers()

        // keep selected modifiers set in sync
        selectedModifiers.clear()
        selectedModifiers.addAll(character.selectedModifiers)

        modifierGroups = character.entities
            .flatMap { it.modifiersGroups }
            .filter { it.target == DnDModifierTargetType.SAVING_THROW || it.target == DnDModifierTargetType.SKILL }
            .sortedBy { it.priority }

        groupIdToGroup = modifierGroups.associateBy { it.id }
        modifierIdToGroupId = modifierGroups
            .flatMap { g -> g.modifiers.map { it.id to g.id } }
            .toMap()
        modifierIdToModifier = modifierGroups
            .flatMap { g -> g.modifiers.map { it.id to it } }
            .toMap()

        val attributes = character.characterAttributes

        // update UI state once
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                character = character.character,
                proficiencyBonus = character.proficiencyBonus,
                attributes = attributes,
                savingThrows = getModifiersInfo(DnDModifierTargetType.SAVING_THROW) { attributes[it] },
                skills = getModifiersInfo(DnDModifierTargetType.SKILL) { attributes[it.attribute] }
            )
        }
    }

    private fun buildExtInfo(
        modifier: DnDModifier,
        group: DnDModifiersGroup,
        selectable: Boolean,
        selected: Boolean
    ) = ModifierExtendedInfo(
        groupId = group.id,
        modifier = modifier,
        operation = group.operation,
        valueSource = group.valueSource,
        state = UiSelectableState(selectable = selectable, selected = selected)
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
        val attributesSnapshot = uiState.value.attributes

        _uiState.update { state ->
            when (target) {
                DnDModifierTargetType.SAVING_THROW -> state.copy(
                    savingThrows = getModifiersInfo(DnDModifierTargetType.SAVING_THROW) { attribute -> attributesSnapshot[attribute] }
                )

                DnDModifierTargetType.SKILL -> state.copy(
                    skills = getModifiersInfo(DnDModifierTargetType.SKILL) { skill -> attributesSnapshot[skill.attribute] }
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
        val raw = mutableMapOf<String, MutableList<ModifierExtendedInfo>>()

        modifierGroups.forEach { group ->
            if (group.target != target) return@forEach

            val selectedInGroup = group.modifiers.count { it.id in selectedModifiers }
            val groupAllowsExtra = selectedInGroup < group.selectionLimit

            group.modifiers.forEach { modifier ->
                val selected = modifier.id in selectedModifiers
                val selectable = modifier.selectable && (selected || groupAllowsExtra) // TODO: clampMin/clampMax checks could influence selectability

                val ext = buildExtInfo(modifier, group, selectable, selected)
                raw.getOrPut(modifier.target) { mutableListOf() }.add(ext)
            }
        }

        // convert keys that match enum names to the enum
        val modifiersByEnum = mutableMapOf<E, List<ModifierExtendedInfo>>()
        raw.forEach { (k, v) ->
            runCatching { enumValueOf<E>(k) }.getOrNull()?.let { modifiersByEnum[it] = v }
        }

        // For each enum entry compute the final modifier value using calculateModifierSum.
        return modifiersByEnum.mapValues { (enumKey, extList) ->
            val groupsForTarget = modifierGroups.filter { it.target == target }

            val value = calculateModifierSum(
                baseValue = baseValueGetter(enumKey),
                groups = groupsForTarget,
                modifierFilter = { it.target == enumKey.name },
                // Provide group-level value once per group (map group.valueSource -> value)
                groupValueProvider = { group ->
                    when (group.valueSource) {
                        DnDModifierValueSource.CONST -> null
                        DnDModifierValueSource.CHARACTER_LEVEL -> uiState.value.characterLevel?.toDouble()
                        DnDModifierValueSource.ENTITY_LEVEL -> {
                            // ENTITY_LEVEL requires entity context (not available here).
                            // Fallback to null (use modifier.value). Implement lookup if entity-level is required.
                            null
                        }
                        DnDModifierValueSource.PROFICIENCY_BONUS -> uiState.value.proficiencyBonus.toDouble()
                    }
                }
            )

            extList to value
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
            .filter { it.target == DnDModifierTargetType.SAVING_THROW }
            .all { group -> group.modifiers.count { it.id in selectedModifiers } == group.selectionLimit }

        val isSkillsValid = modifierGroups
            .filter { it.target == DnDModifierTargetType.SKILL }
            .all { group -> group.modifiers.count { it.id in selectedModifiers } == group.selectionLimit }

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
}

@Immutable
data class NewCharacterThrowsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,

    val character: CharacterShortInfo = CharacterShortInfo(),
    val characterLevel: Int = 1,
    val proficiencyBonus: Int = proficiencyBonusByLevel(characterLevel),
    val attributes: DnDAttributesGroup = DnDAttributesGroup.Default,

    val savingThrows: Map<Attributes, Pair<List<ModifierExtendedInfo>, Int>> = emptyMap(),
    val skills: Map<Skills, Pair<List<ModifierExtendedInfo>, Int>> = emptyMap()
)

data class UiSelectableState(
    val selectable: Boolean,
    val selected: Boolean
)

data class ModifierExtendedInfo(
    val groupId: Uuid,
    val modifier: DnDModifier,
    val operation: DnDModifierOperation,
    val valueSource: DnDModifierValueSource,
    val state: UiSelectableState
)
