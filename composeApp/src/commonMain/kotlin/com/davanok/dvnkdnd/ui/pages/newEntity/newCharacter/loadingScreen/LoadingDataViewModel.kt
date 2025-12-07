package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.core.CheckingDataStates
import com.davanok.dvnkdnd.core.InternetConnectionException
import com.davanok.dvnkdnd.domain.repositories.remote.ExternalKeyValueRepository
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.finish
import dvnkdnd.composeapp.generated.resources.no_internet_exception
import dvnkdnd.composeapp.generated.resources.state_checking_data
import dvnkdnd.composeapp.generated.resources.state_downloading
import dvnkdnd.composeapp.generated.resources.state_loading
import dvnkdnd.composeapp.generated.resources.state_loading_from_database
import dvnkdnd.composeapp.generated.resources.state_loading_full_entities
import dvnkdnd.composeapp.generated.resources.state_updating_entities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.Uuid

class LoadingDataViewModel(
    private val entitiesBootstrapper: EntitiesBootstrapper,
    private val externalKeyValueRepository: ExternalKeyValueRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoadingDataUiState>(LoadingDataUiState.Loading)
    val uiState: StateFlow<LoadingDataUiState> = _uiState

    // downloadable items

    private fun setCheckingState(state: LoadingDataUiState) = _uiState.update { state }

    private suspend fun loadRequiredEntities(
        requiredEntities: List<Uuid>
    ) {
        var hasError = false
        entitiesBootstrapper.checkAndLoadEntities(requiredEntities)
            .catch {
                hasError = true
                setCheckingState(LoadingDataUiState.Error(it))
            }.collect {
                setCheckingState(
                    when (it) {
                        CheckingDataStates.LOAD_FROM_DATABASE -> LoadingDataUiState.LoadFromDatabase
                        CheckingDataStates.CHECKING -> LoadingDataUiState.Checking
                        CheckingDataStates.LOADING_DATA -> LoadingDataUiState.LoadingData
                        CheckingDataStates.UPDATING -> LoadingDataUiState.Updating
                        CheckingDataStates.FINISH -> LoadingDataUiState.Finish
                    }
                )
            }
        if (!hasError) {
            setCheckingState(LoadingDataUiState.Finish)
        }
    }

    fun checkRequiredEntities() = viewModelScope.launch {
        setCheckingState(LoadingDataUiState.LoadFromServer)
        externalKeyValueRepository.getRequiredEntities().onSuccess {
            loadRequiredEntities(it)
        }.onFailure {
            if (it == InternetConnectionException()) setCheckingState(LoadingDataUiState.NoInternet)
            else setCheckingState(LoadingDataUiState.Error(it))
        }
    }

    init {
        checkRequiredEntities()
    }
}


sealed class LoadingDataUiState(val stringRes: StringResource) {
    data object Loading : LoadingDataUiState(Res.string.state_loading)
    data object LoadFromServer : LoadingDataUiState(Res.string.state_downloading)
    data object LoadFromDatabase : LoadingDataUiState(Res.string.state_loading_from_database)
    data object Checking : LoadingDataUiState(Res.string.state_checking_data)
    data object LoadingData : LoadingDataUiState(Res.string.state_loading_full_entities)
    data object Updating : LoadingDataUiState(Res.string.state_updating_entities)
    data object Finish : LoadingDataUiState(Res.string.finish)
    data object NoInternet : LoadingDataUiState(Res.string.no_internet_exception)

    data class Error(val exception: Throwable) : LoadingDataUiState(Res.string.error)

    companion object {
        val entries = listOf(
            Loading,
            LoadFromServer,
            LoadFromDatabase,
            Checking,
            LoadingData,
            Updating,
            Finish
        )
    }
}