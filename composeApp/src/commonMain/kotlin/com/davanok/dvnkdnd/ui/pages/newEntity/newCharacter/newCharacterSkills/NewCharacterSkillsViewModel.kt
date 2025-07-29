package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.not_all_available_skills_selected
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NewCharacterSkillsViewModel(
    private val newCharacterViewModel: NewCharacterViewModel,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterSkillsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterSkillsUiState> = _uiState

    private var allEntitiesWithSkills = emptyList<DnDEntityWithSkills>()
    private lateinit var skillsState: SkillsTableState

    fun loadCharacterWithSkills() {
        val character = newCharacterViewModel.getCharacterWithAllSkills()
        allEntitiesWithSkills = character.classes +
                listOfNotNull(
                    character.race,
                    character.subRace,
                    character.background,
                    character.subBackground
                )
        skillsState = SkillsTableState(
            allEntitiesWithSkills,
            character.selectedSkills.toSet()
        )

        _uiState.update {
            it.copy(
                isLoading = false,
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

    fun commit(onSuccess: () -> Unit) {
        if (skillsState.validateSelectedSkills())
            newCharacterViewModel.setCharacterSkills(skillsState.getSelectedSkills().toList())
                .onFailure { thr ->
                    _uiState.update {
                        it.copy(
                            error = UiError.Critical(
                                message = Res.string.saving_data_error,
                                exception = thr
                            )
                        )
                    }
                }.onSuccess {
                    onSuccess()
                }
        else
            _uiState.update {
                it.copy(error = UiError.Warning(Res.string.not_all_available_skills_selected))
            }
    }

    init {
        loadCharacterWithSkills()
    }
}

data class NewCharacterSkillsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val skills: Map<Skills, UiSkillState> = emptyMap()
)
