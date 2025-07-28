package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.types.CheckingDataStates
import com.davanok.dvnkdnd.data.repositories.ExternalKeyValueRepository
import com.davanok.dvnkdnd.data.repositories.UtilsDataRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.finish
import dvnkdnd.composeapp.generated.resources.state_checking_data
import dvnkdnd.composeapp.generated.resources.state_downloading
import dvnkdnd.composeapp.generated.resources.state_loading
import dvnkdnd.composeapp.generated.resources.state_loading_from_database
import dvnkdnd.composeapp.generated.resources.state_loading_full_entities
import dvnkdnd.composeapp.generated.resources.state_updating_entities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.Uuid

class LoadingDataViewModel(
    private val utilsDataRepository: UtilsDataRepository,
    private val externalKeyValueRepository: ExternalKeyValueRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(LoadingDataUiState.LOADING)
    val uiState: StateFlow<LoadingDataUiState> = _uiState

    // downloadable items

    private fun setCheckingState(state: LoadingDataUiState) = _uiState.update { state }

    private suspend fun loadRequiredEntities(
        requiredEntities: List<Uuid>,
        onSuccess: () -> Unit
    ) {
        var hasError = false
        utilsDataRepository.checkAndLoadEntities(requiredEntities).collect {
            if (it == CheckingDataStates.ERROR) hasError = true
            setCheckingState(
                when (it) {
                    CheckingDataStates.LOAD_FROM_DATABASE -> LoadingDataUiState.LOAD_FROM_DATABASE
                    CheckingDataStates.CHECKING -> LoadingDataUiState.CHECKING
                    CheckingDataStates.LOADING_DATA -> LoadingDataUiState.LOADING_DATA
                    CheckingDataStates.UPDATING -> LoadingDataUiState.UPDATING
                    CheckingDataStates.FINISH -> LoadingDataUiState.FINISH
                    CheckingDataStates.ERROR -> LoadingDataUiState.ERROR
                }
            )
        }
        if (!hasError) {
            setCheckingState(LoadingDataUiState.FINISH)
            onSuccess()
        }
    }
    
    fun checkRequiredEntities(onSuccess: () -> Unit) = viewModelScope.launch {
        setCheckingState(LoadingDataUiState.LOAD_FROM_SERVER)
        externalKeyValueRepository.getRequiredEntities().onSuccess {
            loadRequiredEntities(it, onSuccess)
        }.onFailure {
            setCheckingState(LoadingDataUiState.ERROR)
        }
    }
}


enum class LoadingDataUiState(val text: StringResource) {
    LOADING(Res.string.state_loading),
    LOAD_FROM_SERVER(Res.string.state_downloading),
    LOAD_FROM_DATABASE(Res.string.state_loading_from_database),
    CHECKING(Res.string.state_checking_data),
    LOADING_DATA(Res.string.state_loading_full_entities),
    UPDATING(Res.string.state_updating_entities),
    FINISH(Res.string.finish),
    ERROR(Res.string.error)
}