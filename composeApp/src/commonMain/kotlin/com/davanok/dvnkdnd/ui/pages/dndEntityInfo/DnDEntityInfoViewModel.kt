package com.davanok.dvnkdnd.ui.pages.dndEntityInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_entity_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

@AssistedInject
class DnDEntityInfoViewModel(
    @Assisted entityId: Uuid,
    private val repository: BrowseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DnDEntityInfoUiState(isLoading = true))
    val uiState: StateFlow<DnDEntityInfoUiState> = _uiState

    fun loadEntityInfo(entityId: Uuid) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        repository.loadEntityFullInfo(entityId).onFailure { thr ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = UiError.Critical(
                        message = getString(Res.string.loading_entity_error),
                        exception = thr
                    )
                )
            }
        }.onSuccess { entity ->
            _uiState.update { it.copy(isLoading = false, entity = entity) }
        }
    }

    init {
        loadEntityInfo(entityId)
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted entityId: Uuid): DnDEntityInfoViewModel
    }
}

data class DnDEntityInfoUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val entity: DnDFullEntity? = null
)