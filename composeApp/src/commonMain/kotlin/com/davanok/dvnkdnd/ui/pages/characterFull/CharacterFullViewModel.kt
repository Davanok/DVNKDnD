package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.core.utils.getByKeyPredicate
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.character.CharacterItemLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterStateLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import com.davanok.dvnkdnd.ui.components.UiMessage
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.app_name
import dvnkdnd.composeapp.generated.resources.character_add_item_dialog_title
import dvnkdnd.composeapp.generated.resources.character_add_state_dialog_title
import dvnkdnd.composeapp.generated.resources.character_full_attacks_page_title
import dvnkdnd.composeapp.generated.resources.character_full_attributes_page_title
import dvnkdnd.composeapp.generated.resources.character_full_items_page_title
import dvnkdnd.composeapp.generated.resources.character_full_notes_page_title
import dvnkdnd.composeapp.generated.resources.character_full_spell_slots_title
import dvnkdnd.composeapp.generated.resources.character_full_spells_page_title
import dvnkdnd.composeapp.generated.resources.character_full_states_page_title
import dvnkdnd.composeapp.generated.resources.character_health_dialog_title
import dvnkdnd.composeapp.generated.resources.character_main_entities_dialog_title
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

class CharacterFullViewModel(
    private val characterId: Uuid,
    private val bootstrapper: EntitiesBootstrapper,
    private val repository: CharactersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterFullUiState(isLoading = true))
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
        initialValue = CharacterFullUiState(isLoading = true)
    )

    private fun addMessage(message: UiMessage) =
        _uiState.update { it.copy(messages = it.messages + message) }

    fun removeMessage(messageId: Uuid) = _uiState.update { state ->
        state.copy(messages = state.messages.filter { it.id == messageId })
    }

    private fun <T> Result<T>.handleResult(
        successMessage: (T) -> String?,
        failureMessage: (Throwable) -> String?
    ) : Result<T> =
        onSuccess {
            val text = successMessage(it) ?: return@onSuccess
            val message = UiMessage.Success(message = text)
            addMessage(message)
        }.onFailure {
            val text = failureMessage(it) ?: return@onFailure
            val message = UiMessage.Error(message = text, error = it)
            addMessage(message)
        }

    fun updateHealth(health: CharacterHealth) = viewModelScope.launch {
        repository.setCharacterHealth(characterId, health)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }
    fun updateOrNewNote(note: CharacterNote) = viewModelScope.launch {
        repository.setCharacterNote(characterId, note)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }
    fun deleteNote(note: CharacterNote) = viewModelScope.launch {
        repository.deleteCharacterNote(note.id)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }

    fun setUsedSpellsCount(typeId: Uuid?, level: Int, count: Int) = viewModelScope.launch {
        val arraySize = level
        val index = level - 1

        val minCount = 0
        val maxCount = uiState.value.character?.spellSlots?.getByKeyPredicate{ it?.id == typeId }?.get(index)

        val was = uiState.value.character?.usedSpells?.get(typeId)
        val new = count.coerceIn(minCount, maxCount)

        val newArray = when {
            was == null -> intArrayOf(arraySize)
            was.getOrNull(index) == new -> return@launch
            was.size < arraySize -> was.copyOf(arraySize)
            else -> was.copyOf()
        }

        newArray[index] = count.coerceIn(minCount, maxCount)

        repository.setCharacterUsedSpells(
            characterId = characterId,
            typeId = typeId,
            usedSpells = newArray
        ).handleResult(
            successMessage = { "TODO" },
            failureMessage = { "TODO" }
        )
    }

    fun updateCharacterItem(item: CharacterItem) = viewModelScope.launch {
        repository
            .setCharacterItem(characterId, item.toCharacterItemLink())
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }
    fun newCharacterItem(itemId: Uuid) = viewModelScope.launch {
        bootstrapper.checkAndLoadEntity(itemId)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
            .onFailure { return@launch }

        val item = CharacterItemLink(
            equipped = false,
            attuned = false,
            count = 1,
            item = itemId
        )
        repository.setCharacterItem(characterId, item)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }

    fun newCharacterState(stateId: Uuid, sourceId: Uuid?) = viewModelScope.launch {
        bootstrapper.checkAndLoadEntity(stateId)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
            .onFailure { return@launch }


        val state = CharacterStateLink(
            stateId = stateId,
            sourceId = sourceId
        )
        repository.setCharacterState(characterId, state)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }

    fun activateCharacterItem(item: CharacterItem, activation: FullItemActivation) = viewModelScope.launch {
        repository
            .activateCharacterItem(characterId, item.toCharacterItemLink(), activation)
            .handleResult(
                successMessage = { "TODO" },
                failureMessage = { "TODO" }
            )
    }

    fun action(action: CharacterFullScreenContract) = when (action) {
        is CharacterFullScreenContract.DeleteNote -> deleteNote(action.note)
        is CharacterFullScreenContract.SetHealth -> updateHealth(action.health)
        is CharacterFullScreenContract.SetUsedSpellsCount -> setUsedSpellsCount(action.typeId, action.level, action.count)
        is CharacterFullScreenContract.UpdateCharacterItem -> updateCharacterItem(action.item)
        is CharacterFullScreenContract.UpdateOrNewNote -> updateOrNewNote(action.note)
        is CharacterFullScreenContract.ActivateCharacterItem -> activateCharacterItem(action.item, action.activation)
        is CharacterFullScreenContract.AddItem -> newCharacterItem(action.item.id)
        is CharacterFullScreenContract.AddState -> newCharacterState(action.state.id, null)
    }
}

data class CharacterFullUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterFull? = null,
    val messages: List<UiMessage> = emptyList()
) {
    enum class Page(val stringRes: StringResource) {
        ATTRIBUTES(Res.string.character_full_attributes_page_title),
        ATTACKS(Res.string.character_full_attacks_page_title),
        ITEMS(Res.string.character_full_items_page_title),
        SPELLS(Res.string.character_full_spells_page_title),
        SPELL_SLOTS(Res.string.character_full_spell_slots_title),
        STATES(Res.string.character_full_states_page_title),
        NOTES(Res.string.character_full_notes_page_title),
    }
    enum class Dialog(val titleStringRes: StringResource) {
        HEALTH(Res.string.character_health_dialog_title),
        MAIN_ENTITIES(Res.string.character_main_entities_dialog_title),
        ADD_ITEM(Res.string.character_add_item_dialog_title),
        ADD_STATE(Res.string.character_add_state_dialog_title),
        NONE(Res.string.app_name)
    }
}