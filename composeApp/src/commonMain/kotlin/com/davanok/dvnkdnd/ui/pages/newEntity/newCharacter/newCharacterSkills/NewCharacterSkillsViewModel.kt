package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.model.ui.UiSelectableState
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.not_all_available_skills_selected
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class NewCharacterSkillsViewModel(
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterSkillsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterSkillsUiState> = _uiState
    private lateinit var skillsState: SkillsTableState

    fun loadCharacterWithSkills() {
        val character = newCharacterViewModel.getCharacterWithAllSkills()
        skillsState = SkillsTableState(
            character.entities,
            character.selectedSkills.toSet()
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                character = newCharacterViewModel.getCharacterShortInfo(),
                proficiencyBonus = character.proficiencyBonus,
                selectionLimit = character.entities.sumOf { e -> e.selectionLimit },
                stats = character.stats,
                skills = skillsState.getDisplayItems()
            )
        }
    }

    fun removeWarning() = _uiState.update {
        it.copy(error = null)
    }

    fun selectSkill(skill: Skills) {
        if (skillsState.select(skill)) _uiState.update {
            it.copy(skills = skillsState.getDisplayItems())
        }
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        if (skillsState.validateSelectedSkills())
            newCharacterViewModel.setCharacterSkills(skillsState.getSelectedSkills().toList())
                .onFailure { thr ->
                    _uiState.update {
                        it.copy(
                            error = UiError.Critical(
                                message = getString(Res.string.saving_data_error),
                                exception = thr
                            )
                        )
                    }
                }.onSuccess {
                    onSuccess()
                }
        else
            _uiState.update {
                it.copy(
                    error = UiError.Warning(
                        message = getString(Res.string.not_all_available_skills_selected)
                    )
                )
            }
    }

    init {
        loadCharacterWithSkills()
    }
}

data class NewCharacterSkillsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterShortInfo = CharacterShortInfo(),
    val proficiencyBonus: Int = 0,
    val selectionLimit: Int = 0,
    val stats: DnDAttributesGroup = DnDAttributesGroup.Default,
    val skills: Map<Skills, UiSelectableState> = emptyMap()
)
