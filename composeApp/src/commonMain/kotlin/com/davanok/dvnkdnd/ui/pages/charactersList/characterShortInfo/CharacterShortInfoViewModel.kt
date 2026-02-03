package com.davanok.dvnkdnd.ui.pages.charactersList.characterShortInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.ui.model.UiError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_character_error
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

@AssistedInject
class CharacterShortInfoViewModel(
    @Assisted characterId: Uuid,
    repository: CharactersRepository
) : ViewModel() {
    private val _character = repository.getFullCharacterFlow(characterId)

    val uiState = _character.map { result ->
        result.fold(
            onSuccess = {
                CharacterShortInfoUiState(character = it)
            },
            onFailure = {
                CharacterShortInfoUiState(
                    error = UiError.Critical(
                        message = getString(Res.string.loading_character_error),
                        exception = it
                    )
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharacterShortInfoUiState(isLoading = true)
    )


    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted characterId: Uuid): CharacterShortInfoViewModel
    }
}

data class CharacterShortInfoUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val character: CharacterFull? = null
)