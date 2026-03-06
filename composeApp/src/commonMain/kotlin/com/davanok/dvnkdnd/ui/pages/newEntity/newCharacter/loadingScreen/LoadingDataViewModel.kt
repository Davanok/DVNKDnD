package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.loadingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.core.InternetConnectionException
import com.davanok.dvnkdnd.domain.repositories.remote.ExternalKeyValueRepository
import com.davanok.dvnkdnd.domain.usecases.entities.bootstrap.EntitiesBootstrapEvent
import com.davanok.dvnkdnd.domain.usecases.entities.bootstrap.EntitiesBootstrapper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.finish
import dvnkdnd.composeapp.generated.resources.no_internet_exception
import dvnkdnd.composeapp.generated.resources.state_downloading
import dvnkdnd.composeapp.generated.resources.state_loading
import dvnkdnd.composeapp.generated.resources.state_loading_from_database
import dvnkdnd.composeapp.generated.resources.state_loading_full_entities
import dvnkdnd.composeapp.generated.resources.state_updating_entities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.Uuid

@Inject
@ViewModelKey(LoadingDataViewModel::class)
@ContributesIntoMap(AppScope::class)
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
    ) = entitiesBootstrapper
        .checkAndLoadEntities(requiredEntities)
        .map {
            when (it) {
                EntitiesBootstrapEvent.Started -> LoadingDataUiState.LoadFromDatabase
                is EntitiesBootstrapEvent.LocalChecked -> LoadingDataUiState.LoadingData
                is EntitiesBootstrapEvent.RemoteLoaded -> LoadingDataUiState.Updating
                EntitiesBootstrapEvent.Saved,
                EntitiesBootstrapEvent.Finished -> LoadingDataUiState.Finish
            }
        }
        .catch { setCheckingState(LoadingDataUiState.Error(it)) }
        .collect { state -> setCheckingState(state) }

    fun checkRequiredEntities() = viewModelScope.launch {
        setCheckingState(LoadingDataUiState.LoadFromServer)
        externalKeyValueRepository.getRequiredEntities().onSuccess {
            loadRequiredEntities(it)
        }.onFailure {
            if (it is InternetConnectionException) setCheckingState(LoadingDataUiState.NoInternet)
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
            LoadingData,
            Updating,
            Finish
        )
    }
}