package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.dnd_enums.MainSources
import com.davanok.dvnkdnd.data.model.util.WhileUiSubscribed
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

class NewCharacterViewModel(
    private val repository: NewCharacterRepository,
    private val filesRepository: FilesRepository,
    private val browseRepository: BrowseRepository
) : ViewModel() {

    private val _message = MutableStateFlow<StringResource?>(null)
    private val _searchSheetEntityType = MutableStateFlow<DnDEntityTypes>(DnDEntityTypes.NONE)

    val uiState: StateFlow<NewCharacterUiState> = combine(
        _message, _searchSheetEntityType
    ) { message, sheetContent ->
        NewCharacterUiState(
            isLoading = false,
            message = message,
            showSearchSheet = sheetContent != DnDEntityTypes.NONE
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NewCharacterUiState(isLoading = true)
    )
    fun hideSearchSheet(selectedEntity: DnDEntityMin?, selectedSubEntity: DnDEntityMin?) {
        _searchSheetEntityType.value.let {
            when (it) {
                DnDEntityTypes.NONE -> {}
                DnDEntityTypes.CLASS -> {
                    setCharacterClass(selectedEntity)
                    setCharacterSubClass(selectedSubEntity)
                }
                DnDEntityTypes.RACE -> {
                    setCharacterRace(selectedEntity)
                    setCharacterSubRace(selectedSubEntity)
                }
                DnDEntityTypes.BACKGROUND -> setCharacterBackground(selectedEntity)
                else -> throw IllegalArgumentException()
            }
        }
        _searchSheetEntityType.value = DnDEntityTypes.NONE
    }
    fun openSearchSheet(content: DnDEntityTypes, query: String) {
        _searchSheetEntityType.value = content
        setSearchQuery(query)

        val needLoad = when(content) {
            DnDEntityTypes.CLASS -> _searchClassEntities.value.isEmpty()
            DnDEntityTypes.RACE -> _searchRaceEntities.value.isEmpty()
            DnDEntityTypes.BACKGROUND -> _searchBackgroundEntities.value.isEmpty()
            else -> false
        }
        if (needLoad) loadSearchEntities()
    }

    // Character setters

    private val _characterImages = MutableStateFlow<List<Path>>(emptyList())
    private val _characterMainImage = MutableStateFlow<Path?>(null)
    private val _characterName = MutableStateFlow("")
    private val _characterDescription = MutableStateFlow("")
    private val _characterCls = MutableStateFlow<DnDEntityMin?>(null)
    private val _characterSubCls = MutableStateFlow<DnDEntityMin?>(null)
    private val _characterRace = MutableStateFlow<DnDEntityMin?>(null)
    private val _characterSubRace = MutableStateFlow<DnDEntityMin?>(null)
    private val _characterBackground = MutableStateFlow<DnDEntityMin?>(null)

    @Suppress("UNCHECKED_CAST")
    val newCharacterState: StateFlow<NewCharacterState> = combine(
        _characterImages,
        _characterMainImage,
        _characterName,
        _characterDescription,
        _characterCls,
        _characterSubCls,
        _characterRace,
        _characterSubRace,
        _characterBackground
    ) { args ->
        NewCharacterState(
            images = args[0] as List<Path>,
            mainImage = args[1] as Path?,
            name = args[2] as String,
            description = args[3] as String,
            cls = args[4] as DnDEntityMin?,
            subCls = args[5] as DnDEntityMin?,
            race = args[6] as DnDEntityMin?,
            subRace = args[7] as DnDEntityMin?,
            background = args[8] as DnDEntityMin?
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NewCharacterState()
    )
    @OptIn(ExperimentalUuidApi::class)
    fun addCharacterImage(value: ByteArray) = viewModelScope.launch {
        val path = filesRepository.getFilename(FilesRepository.Paths.images, "png", true) // png as default in ImageBitmap.toByteArray
        filesRepository.write(value, path)
        val mutable = _characterImages.value.toMutableList()
        mutable.add(0, path)
        _characterImages.value = mutable.toList()
        setCharacterMainImage(path)
    }
    fun removeCharacterImage(value: Path) = viewModelScope.launch {
        filesRepository.delete(value)
        if (_characterMainImage.value == value) _characterMainImage.value = null
        _characterImages.value = _characterImages.value.fastFilter { it != value }
        setCharacterMainImage(_characterImages.value.firstOrNull())
    }
    fun setCharacterMainImage(value: Path?) { _characterMainImage.value = value }
    fun setCharacterName(value: String) { _characterName.value = value }
    fun setCharacterDescription(value: String) { _characterDescription.value = value }
    fun setCharacterClass(value: DnDEntityMin?) {
        _characterCls.value = value
        _characterSubCls.value = null
    }
    fun setCharacterSubClass(value: DnDEntityMin?) { _characterSubCls.value = value }
    fun setCharacterRace(value: DnDEntityMin?) {
        _characterRace.value = value
        _characterSubRace.value = null
    }
    fun setCharacterSubRace(value: DnDEntityMin?) { _characterSubRace.value = value }
    fun setCharacterBackground(value: DnDEntityMin?) { _characterBackground.value = value }

    // downloadable items

    private val _mainClasses = MutableStateFlow<List<DnDEntityMin>>(emptyList())
    private val _mainRaces = MutableStateFlow<List<DnDEntityMin>>(emptyList())
    private val _mainBackgrounds = MutableStateFlow<List<DnDEntityMin>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _subClasses = _characterCls.flatMapLatest { cls ->
        cls?.let {
            flowOf(repository.getEntitiesMinList(DnDEntityTypes.CLASS, it.id))
        } ?: flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = null
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _subRaces = _characterRace.flatMapLatest { race ->
        race?.let {
            flowOf(repository.getEntitiesMinList(DnDEntityTypes.RACE, it.id))
        } ?: flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = null
    )

    init {
        loadMainValues()
    }

    private fun loadMainValues() = viewModelScope.launch {
        _mainClasses.value = repository.getEntitiesMinList(DnDEntityTypes.CLASS)
        _mainRaces.value = repository.getEntitiesMinList(DnDEntityTypes.RACE)
        _mainBackgrounds.value = repository.getEntitiesMinList(DnDEntityTypes.BACKGROUND)
    }

    val downloadableState: StateFlow<DownloadableValuesState> = combine(
        _mainClasses, _subClasses, _mainRaces, _subRaces, _mainBackgrounds
    ) { classes, subClasses, races, subRaces, backgrounds ->
        DownloadableValuesState(
            classes = classes,
            subClasses = subClasses,
            races = races,
            subRaces = subRaces,
            backgrounds = backgrounds
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = DownloadableValuesState(isLoading = true)
    )

    // Search content

    private val isUpdatingEntities = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _searchClassEntities = MutableStateFlow(emptyMap<String, List<DnDEntityWithSubEntities>>())
    private val _searchRaceEntities = MutableStateFlow(emptyMap<String, List<DnDEntityWithSubEntities>>())
    private val _searchBackgroundEntities = MutableStateFlow(emptyMap<String, List<DnDEntityWithSubEntities>>())
    private val _filteredSearchEntities = MutableStateFlow(emptyMap<String, List<DnDEntityWithSubEntities>>())
    
    fun loadSearchEntities() = viewModelScope.launch {
        val getEntities: suspend () -> Map<String, List<DnDEntityWithSubEntities>> = {
            isUpdatingEntities.value = true
            val result = browseRepository
                .loadEntities(_searchSheetEntityType.value)
                .groupBy { it.source }
            isUpdatingEntities.value = false
            result
        }

        when (_searchSheetEntityType.value) {
            DnDEntityTypes.CLASS -> {
                val entities = getEntities()
                _searchClassEntities.value = entities
                _filteredSearchEntities.value = entities
            }
            DnDEntityTypes.RACE -> {
                val entities = getEntities()
                _searchRaceEntities.value = entities
                _filteredSearchEntities.value = entities
            }
            DnDEntityTypes.BACKGROUND -> {
                val entities = getEntities()
                _searchBackgroundEntities.value = entities
                _filteredSearchEntities.value = entities
            }
            else -> throw IllegalArgumentException("illegal to load searchEntities not main types")
        }
    }

    fun setSearchQuery(value: String) {
        _searchQuery.value = value

        val entities = when(_searchSheetEntityType.value) {
            DnDEntityTypes.CLASS -> _searchClassEntities.value
            DnDEntityTypes.RACE -> _searchRaceEntities.value
            DnDEntityTypes.BACKGROUND -> _searchBackgroundEntities.value
            else -> throw IllegalArgumentException("illegal to search entities not main types")
        }

        if (value.isEmpty())
            _filteredSearchEntities.value = entities
        else
            _filteredSearchEntities.value = entities.mapValues { (_, list) ->
                list.fastFilter { it.name.startsWith(value, ignoreCase = true) }
            }.filterValues { it.isNotEmpty() }
    }
    val searchSheetState: StateFlow<SearchSheetUiState> = combine(
        _searchSheetEntityType, _searchQuery, _filteredSearchEntities, isUpdatingEntities
    ) { content, query, groups, isLoading ->
        SearchSheetUiState(
            query = query,
            searchType = content,
            entitiesGroups = groups,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = SearchSheetUiState(isLoading = true)
    )
}

data class NewCharacterUiState(
    val isLoading: Boolean = false,
    val message: StringResource? = null,
    val showSearchSheet: Boolean = false
)

data class DownloadableValuesState(
    val isLoading: Boolean = false,
    val classes: List<DnDEntityMin> = emptyList(),
    val subClasses: List<DnDEntityMin>? = null,
    val races: List<DnDEntityMin> = emptyList(),
    val subRaces: List<DnDEntityMin>? = null,
    val backgrounds: List<DnDEntityMin> = emptyList()
)

data class NewCharacterState(
    val images: List<Path> = emptyList(),
    val mainImage: Path? = null,
    val name: String = "",
    val description: String = "",
    val cls: DnDEntityMin? = null,
    val subCls: DnDEntityMin? = null,
    val race: DnDEntityMin? = null,
    val subRace: DnDEntityMin? = null,
    val background: DnDEntityMin? = null
)

data class SearchSheetUiState(
    val isLoading: Boolean = false,
    val searchType: DnDEntityTypes = DnDEntityTypes.NONE,
    val query: String = "",
    val entitiesGroups: Map<String, List<DnDEntityWithSubEntities>> = emptyMap()
)
