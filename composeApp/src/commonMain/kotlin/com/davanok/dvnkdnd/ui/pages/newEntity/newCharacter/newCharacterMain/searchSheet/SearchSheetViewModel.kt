package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.searchSheet

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_entities_error
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

private const val PAGE_SIZE = 100
private const val TEXT_INPUT_DEBOUNCE = 500L

private data class TypeCache(
    val groups: MutableMap<String, MutableList<DnDEntityWithSubEntities>> = mutableMapOf(),
    var nextPage: Int = 0,
    var hasNext: Boolean = true,
    var isLoading: Boolean = false,
    var loadingJob: Job? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchSheetViewModel(
    private val browseRepository: BrowseRepository
) : ViewModel() {

    // user inputs
    private val _selectedType = MutableStateFlow(DnDEntityTypes.CLASS)
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    private val _error = MutableStateFlow<UiError?>(null)
    // caches keyed by Pair(type, queryKey) where queryKey = query ?: ""
    private val caches = mutableMapOf<Pair<DnDEntityTypes, String>, TypeCache>()

    // Exposed ui state as StateFlow - recomputes when query or selectedType or underlying cache changes
    val uiState: StateFlow<SearchSheetUiState> =
        combine(
            _query.debounce(TEXT_INPUT_DEBOUNCE).map { it.trim() }.distinctUntilChanged(),
            _selectedType,
            _error,
            snapshotFlow { caches.toMap() } // re-evaluate when caches map changes
        ) { currentQuery, currentType, error, _ ->
            Triple(currentQuery, currentType, error)
        }.mapLatest { (currentQuery, currentType, error) ->
            val key = currentType to (currentQuery.ifBlank { "" })
            val cache = caches[key]

            val groups = cache?.groups?.mapValues { it.value.toList() } ?: emptyMap()
            val isLoading = cache?.isLoading == true

            SearchSheetUiState(
                isLoading = isLoading,
                error = error,
                query = currentQuery,
                searchType = currentType,
                entitiesGroups = groups
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SearchSheetUiState(isLoading = true)
        )

    // Public setters
    fun setSearchQuery(value: String) {
        _query.value = value
        // When user actively changes query we want to ensure page 0 is requested automatically.
        // The debounced collector in init will trigger loadPage(0) via loadFirstPageIfNeeded below.
        loadFirstPageIfNeeded()
    }

    fun setSearchType(type: DnDEntityTypes) {
        _selectedType.value = type
        // reset or load for new type
        loadFirstPageIfNeeded()
    }

    /**
     * Load first page for current (type, query) if not present or if it was previously empty.
     * This is non-blocking.
     */
    private fun loadFirstPageIfNeeded() {
        val key = _selectedType.value to (_query.value.trim())
        val cache = caches[key]
        if (cache == null || (cache.nextPage == 0 && cache.groups.isEmpty() && !cache.isLoading)) {
            loadPage(0)
        }
    }

    /**
     * Load page for current (type, query). If page == 0 we reset previous cached result for that key.
     */
    fun loadPage(page: Int) {
        val type = _selectedType.value
        val q = _query.value.trim()
        val key = type to q.ifBlank { "" }

        val cache = caches.getOrPut(key) { TypeCache() }

        // If requested page is before nextPage, we already have it or are loading it -> skip.
        if (page < cache.nextPage && cache.groups.isNotEmpty()) return
        if (cache.isLoading) return

        cache.isLoading = true

        val job = viewModelScope.launch {
            browseRepository.loadEntitiesWithSubPaged(
                entityType = type, page = page, pageSize = PAGE_SIZE, searchQuery = q
            ).onSuccess { paged ->
                // If page == 0, and cache contains prior results for same key, replace them
                if (paged.page == 0) cache.groups.clear()

                // Merge fetched page into groups by source
                val newGroups = paged.items.groupBy { it.source }
                for ((src, list) in newGroups) {
                    val existingList = cache.groups.getOrPut(src) { mutableListOf() }
                    existingList.addAll(list)
                }

                cache.hasNext = paged.hasNext
                cache.nextPage = paged.page + 1
                _error.update { null }
            }.onFailure { thr ->
                cache.hasNext = false
                _error.update {
                    UiError.Warning(
                        message = getString(Res.string.loading_entities_error),
                        exception = thr
                    )
                }
            }
            cache.isLoading = false
            cache.loadingJob = null
            // force emission: we update caches map content; snapshotFlow in combine will re-evaluate automatically
            // mutate caches reference to trigger snapshotFlow change (replace same key)
            caches[key] = cache
        }

        cache.loadingJob = job
    }

    /**
     * Load next page if available for current (type, query).
     */
    fun loadNextIfAvailable(): Boolean {
        val key = _selectedType.value to (_query.value.trim())
        val cache = caches[key] ?: return false
        if (!cache.hasNext || cache.isLoading) return false
        loadPage(cache.nextPage)
        return true
    }

    /**
     * Optional: clear cache for current key (useful when you want to force reload).
     */
    fun refreshCurrent() {
        val key = _selectedType.value to (_query.value.trim())
        caches.remove(key)
        loadPage(0)
    }
}


data class SearchSheetUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val searchType: DnDEntityTypes = DnDEntityTypes.CLASS,
    val query: String = "",
    val entitiesGroups: Map<String, List<DnDEntityWithSubEntities>> = emptyMap(),
)