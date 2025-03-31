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

class DnDEntityInfoViewModel(
    private val repository: BrowseRepository
) : ViewModel() {
    private val _entity = MutableStateFlow<DnDEntityFullInfo?>(null)

    val uiState: StateFlow<DnDEntityInfoUiState> = combine (
        _entity
    ) { (entity) ->
        DnDEntityInfoUiState(
            entity = entity
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = DnDEntityInfoUiState(isLoading = true)
    )

    fun loadEntityInfo(entityType: DnDEntityTypes, entityId: Long) = viewModelScope.launch {
        _entity.value = repository.loadEntityFullInfo(entityType, entityId)
    }
}

data class DnDEntityInfoUiState(
    val isLoading: Boolean = false,
    val entity: DnDEntityFullInfo? = null
)