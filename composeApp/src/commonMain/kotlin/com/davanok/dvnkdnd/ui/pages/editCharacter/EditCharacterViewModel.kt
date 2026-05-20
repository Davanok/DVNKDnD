package com.davanok.dvnkdnd.ui.pages.editCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.local.EditCharacterRepository
import com.davanok.dvnkdnd.domain.usecases.character.characterEntities.CharacterEntitiesUseCase
import com.davanok.dvnkdnd.ui.components.UiMessage
import com.davanok.dvnkdnd.ui.model.UiError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.edit_character_attributes_pane_title
import dvnkdnd.composeapp.generated.resources.edit_character_health_pane_title
import dvnkdnd.composeapp.generated.resources.edit_character_main_pane_title
import dvnkdnd.composeapp.generated.resources.edit_character_modifiers_pane_title
import dvnkdnd.composeapp.generated.resources.failed_to_add_character_entity
import dvnkdnd.composeapp.generated.resources.loading_character_error
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

@AssistedInject
class EditCharacterViewModel(
    @Assisted private val characterId: Uuid,
    private val characterEntitiesUseCase: CharacterEntitiesUseCase,
    private val repository: EditCharacterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditCharacterUiState(isLoading = true))
    private val _character = repository.getFullCharacterFlow(characterId)

    val uiState = combine(_uiState, _character) { uiState, characterResult ->
        characterResult.fold(
            onSuccess = { character ->
                uiState.copy(
                    character = character,
                    isLoading = false
                )
            },
            onFailure = { thr ->
                uiState.copy(
                    error = UiError.Critical(
                        getString(Res.string.loading_character_error),
                        thr
                    ),
                    isLoading = false
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditCharacterUiState(isLoading = true)
    )


    private suspend inline fun <T> Result<T>.handleFailure(
        errorMessage: suspend (Throwable) -> String?
    ) = onFailure { thr ->
        errorMessage(thr)?.let { message ->
            _uiState.update { state ->
                state.copy(
                    messages = _uiState.value.messages + UiMessage.Warning(message)
                )
            }
        }
    }


    fun showAddEntityDialog(entityType: DnDEntityTypes) = _uiState.update {
        it.copy(addEntityDialog = entityType)
    }

    fun hideAddEntityDialog() = _uiState.update {
        it.copy(addEntityDialog = null)
    }

    fun addEntity(entity: DnDEntityMin, subEntity: DnDEntityMin?) = viewModelScope.launch {
        characterEntitiesUseCase
            .addCharacterEntity(characterId, entity, subEntity)
            .handleFailure { getString(Res.string.failed_to_add_character_entity) }
    }

    fun removeMessage(messageId: Uuid) = _uiState.update { state ->
        state.copy(messages = state.messages.filter { it.id != messageId })
    }

    fun setPage(page: EditCharacterUiState.Page) = _uiState.update {
        it.copy(currentPage = page)
    }

    fun updateCharacterBase(character: CharacterBase) = viewModelScope.launch {
        repository
            .setCharacterBase(character)
            .handleFailure { getString(Res.string.saving_data_error) }
    }

    fun updateAttributes(attributes: AttributesGroup) = viewModelScope.launch {
        repository.setCharacterAttributes(characterId, attributes)
            .handleFailure { getString(Res.string.saving_data_error) }
    }

    fun setModifierSelection(modifier: DnDModifier, selected: Boolean) = viewModelScope.launch {
        repository.setModifierSelection(characterId, modifier, selected)
            .handleFailure { getString(Res.string.saving_data_error) }
    }

    fun setCharacterCustomModifier(modifier: CharacterCustomModifier) = viewModelScope.launch {
        repository.setCustomModifier(characterId, modifier)
            .handleFailure { getString(Res.string.saving_data_error) }
    }

    fun deleteCharacterCustomModifier(modifier: CharacterCustomModifier) = viewModelScope.launch {
        repository.deleteCustomModifier(characterId, modifier)
            .handleFailure { getString(Res.string.saving_data_error) }
    }

    fun setOptionalValues(values: CharacterOptionalValues) = viewModelScope.launch {
        repository.setCharacterOptionalValues(characterId, values)
            .handleFailure { getString(Res.string.saving_data_error) }
    }
    fun removeCharacterEntity(entity: DnDEntityMin) = viewModelScope.launch {
        characterEntitiesUseCase.removeCharacterEntity(characterId, entity)
    }
    fun setCharacterMainEntityLevel(entity: DnDEntityMin, level: Int) = viewModelScope.launch {
        repository.setCharacterMainEntityLevel(characterId, entity, level)
    }

    fun eventSink(event: EditCharacterScreenEvent) {
        when (event) {
            is EditCharacterScreenEvent.ShowAddEntityDialog -> showAddEntityDialog(event.entityType)
            EditCharacterScreenEvent.HideAddEntityDialog -> hideAddEntityDialog()
            is EditCharacterScreenEvent.AddCharacterEntity -> addEntity(event.entity, event.subEntity)

            is EditCharacterScreenEvent.UpdateCharacterBase -> updateCharacterBase(event.character)
            is EditCharacterScreenEvent.UpdateAttributes -> updateAttributes(event.attributes)
            is EditCharacterScreenEvent.SetModifierSelection -> setModifierSelection(event.modifier, event.selected)

            is EditCharacterScreenEvent.SetCharacterCustomModifier -> setCharacterCustomModifier(event.modifier)

            is EditCharacterScreenEvent.DeleteCharacterCustomModifier -> deleteCharacterCustomModifier(event.modifier)

            is EditCharacterScreenEvent.UpdateOptionalValues -> setOptionalValues(event.values)
            is EditCharacterScreenEvent.RemoveCharacterEntity -> removeCharacterEntity(event.entity)
            is EditCharacterScreenEvent.SetCharacterMainEntityLevel -> setCharacterMainEntityLevel(event.entity, event.level)
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted characterId: Uuid): EditCharacterViewModel
    }
}

data class EditCharacterUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterFull? = null,
    val addEntityDialog: DnDEntityTypes? = null,
    val messages: List<UiMessage> = emptyList(),
    val currentPage: Page = Page.entries.first()
) {
    enum class Page(val stringRes: StringResource) {
        MAIN(Res.string.edit_character_main_pane_title),
        ATTRIBUTES(Res.string.edit_character_attributes_pane_title),
        MODIFIERS(Res.string.edit_character_modifiers_pane_title),
        HEALTH(Res.string.edit_character_health_pane_title),
    }
}