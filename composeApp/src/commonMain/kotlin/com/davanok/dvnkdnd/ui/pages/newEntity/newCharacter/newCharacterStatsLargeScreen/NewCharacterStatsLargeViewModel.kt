package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStatsLargeScreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.types.UiSelectableState
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSavingThrows.SavingThrowsTableState
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills.SkillsTableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NewCharacterStatsLargeViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterStatsLargeUiState())
    val uiState: StateFlow<NewCharacterStatsLargeUiState> = _uiState

    fun removeError() = _uiState.update { it.copy(error = null) }

    private lateinit var savingThrowsState: SavingThrowsTableState
    private lateinit var skillsState: SkillsTableState

    fun loadCharacter() {
        val character = newCharacterViewModel.getCharacterWithSavingThrowsAndSkills()
        savingThrowsState = SavingThrowsTableState(
            character.savingThrowsEntities,
            character.selectedSavingThrows.toSet()
        )
        skillsState = SkillsTableState(
            character.skillsEntities,
            character.selectedSkills.toSet()
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                character = character.character,
                proficiencyBonus = character.proficiencyBonus,
                stats = character.stats,
                savingThrowsSelectionLimit = character.savingThrowsEntities
                    .sumOf { e -> e.selectionLimit },
                savingThrows = savingThrowsState.getDisplayItems(),
                skillsSelectionLimit = character.skillsEntities
                    .sumOf { e -> e.selectionLimit },
                skills = skillsState.getDisplayItems()
            )
        }
    }

    fun selectSavingThrow(stat: Stats) {
        if (savingThrowsState.select(stat)) _uiState.update {
            it.copy(savingThrows = savingThrowsState.getDisplayItems())
        }
    }
    fun selectSkill(skill: Skills) {
        if (skillsState.select(skill)) _uiState.update {
            it.copy(skills = skillsState.getDisplayItems())
        }
    }

    fun commit(onSuccess: () -> Unit) {

    }

    init {
        loadCharacter()
    }
}

@Immutable
data class NewCharacterStatsLargeUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,

    val character: CharacterShortInfo = CharacterShortInfo(),
    val proficiencyBonus: Int = 0,
    val stats: DnDModifiersGroup = DnDModifiersGroup.Default,

    val savingThrowsSelectionLimit: Int = 0,
    val savingThrows: Map<Stats, UiSelectableState> = emptyMap(),

    val skillsSelectionLimit: Int = 0,
    val skills: Map<Skills, UiSelectableState> = emptyMap()
)
