package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterSkills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_characters_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.Uuid

class NewCharacterSkillsViewModel(
    private val repository: CharactersRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterSkillsUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterSkillsUiState> = _uiState

    private var allEntitiesWithSkills = emptyList<DnDEntityWithSkills>()
    private lateinit var skillsState: SkillsTableState

    fun loadCharacterWithSkills(characterId: Uuid) = viewModelScope.launch {
        repository.getCharacterWithAllSkills(characterId).onFailure {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                criticalError = Res.string.loading_characters_error
            )
        }.onSuccess { character ->
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
    }

    fun selectSkill(skill: Skills) {
        if (skillsState.select(skill)) _uiState.update {
            it.copy(skills = skillsState.getDisplayItems())
        }
    }
}

data class NewCharacterSkillsUiState(
    val isLoading: Boolean = false,
    val criticalError: StringResource? = null,
    val skills: Map<Skills, UiSkillState> = emptyMap()
)
