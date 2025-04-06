@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.ui.pages.dndEntityInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.util.WhileUiSubscribed
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DnDEntityInfoViewModel(
    private val repository: BrowseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DnDEntityInfoUiState(isLoading = true))
    val uiState: StateFlow<DnDEntityInfoUiState> = _uiState

    fun loadEntityInfo(entityId: Uuid) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val entity = repository.loadEntityFullInfo(entityId)
        _uiState.value = _uiState.value.copy(isLoading = false, entity = entity)
    }
}

data class DnDEntityInfoUiState(
    val isLoading: Boolean = false,
    val entity: DnDEntityFullInfo? = null
)