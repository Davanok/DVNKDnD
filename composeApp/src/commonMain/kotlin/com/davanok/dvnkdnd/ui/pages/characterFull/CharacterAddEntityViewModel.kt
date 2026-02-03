package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.usecases.entities.BrowseEntitiesUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

@AssistedInject
class CharacterAddEntityViewModel(
    @Assisted private val entitiesType: DnDEntityTypes,
    private val browseEntitiesUseCase: BrowseEntitiesUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val entitiesFlow = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            browseEntitiesUseCase.getEntitiesMin(
                entitiesType,
                query.ifBlank { null }
            )
        }.cachedIn(viewModelScope)

    fun setSearchQuery(query: String) = _searchQuery.update { query }



    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted entitiesType: DnDEntityTypes): CharacterAddEntityViewModel
    }
}