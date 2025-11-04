package com.davanok.dvnkdnd.ui.pages.dndEntityInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_entity_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.uuid.Uuid

class DnDEntityInfoViewModel(
    entityId: Uuid,
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
}

data class DnDEntityInfoUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val entity: DnDFullEntity? = null
)