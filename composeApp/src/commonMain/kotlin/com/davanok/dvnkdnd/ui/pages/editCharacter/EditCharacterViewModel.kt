package com.davanok.dvnkdnd.ui.pages.editCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.repositories.local.EditCharacterRepository
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
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
import dvnkdnd.composeapp.generated.resources.loading_character_error
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
    private val bootstrapper: EntitiesBootstrapper,
    private val repository: EditCharacterRepository
): ViewModel() {
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

    fun removeMessage(messageId: Uuid) = _uiState.update { state ->
        state.copy(messages = state.messages.filter { it.id != messageId })
    }
    fun setPage(page: EditCharacterUiState.Page) = _uiState.update {
        it.copy(currentPage = page)
    }

    fun updateCharacterBase(character: CharacterBase) = viewModelScope.launch {
        TODO()
    }
    fun updateAttributes(attributes: AttributesGroup) = viewModelScope.launch {
        TODO()
    }

    fun eventSink(event: EditCharacterScreenEvent) {
        when (event) {
            is EditCharacterScreenEvent.UpdateCharacterBase -> updateCharacterBase(event.character)
            is EditCharacterScreenEvent.UpdateAttributes -> updateAttributes(event.attributes)
            else -> { TODO() }

        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted characterId: Uuid): EditCharacterViewModel
    }
}

data class EditCharacterUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterFull? = null,
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