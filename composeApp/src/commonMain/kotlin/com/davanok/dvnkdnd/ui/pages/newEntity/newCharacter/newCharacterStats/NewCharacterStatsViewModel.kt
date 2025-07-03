package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.runtime.Stable
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifierBonus
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
    private val repository: CharactersRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterStatsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterStatsUiState> = _uiState

    private var modifierInfo: Map<Uuid, Pair<Int, Set<Uuid>>> = emptyMap()

    fun selectCreationOption(option: StatsCreationOptions) {
        _uiState.value = _uiState.value.copy(selectedCreationOptions = option)
    }

    fun loadCharacterWithModifiers(characterId: Uuid) = viewModelScope.launch {
        var character = repository.getCharacterWithModifiers(characterId)
        if (character.characterStats == null)
            character = character.copy(
                characterStats = DnDModifiersGroup.Default
            )
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
            selectedModifiersBonuses = character.selectedModifiers.toSet(),
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

    val modifiers: DnDModifiersGroup = DnDModifiersGroup.Default,
    val selectedModifiersBonuses: Set<Uuid> = emptySet(),
    @Stable val allEntitiesWithModifiers: List<DnDEntityWithModifiers> = emptyList()
)
