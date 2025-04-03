package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.util.WhileUiSubscribed
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    fun hideSearchSheet(selectedEntity: DnDEntityWithSubEntities?, selectedSubEntity: DnDEntityMin?) {
        _searchSheetEntityType.value.let {
            when (it) {
                DnDEntityTypes.CLASS -> setCharacterClass(selectedEntity, selectedSubEntity)
                DnDEntityTypes.RACE -> setCharacterRace(selectedEntity, selectedSubEntity)
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
    private val _characterCls = MutableStateFlow<DnDEntityWithSubEntities?>(null)
    private val _characterSubCls = MutableStateFlow<DnDEntityMin?>(null)
    private val _characterRace = MutableStateFlow<DnDEntityWithSubEntities?>(null)
    private val _characterSubRace = MutableStateFlow<DnDEntityMin?>(null)
    private val _characterBackground = MutableStateFlow<DnDEntityWithSubEntities?>(null)
    private val _characterSubBackground = MutableStateFlow<DnDEntityMin?>(null)

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
        _characterBackground,
        _characterSubBackground
    ) { args ->
        Napier.d { "update new character state" }
        NewCharacterState(
            images = args[0] as List<Path>,
            mainImage = args[1] as Path?,
            name = args[2] as String,
            description = args[3] as String,
            cls = args[4] as DnDEntityWithSubEntities?,
            subCls = args[5] as DnDEntityMin?,
            race = args[6] as DnDEntityWithSubEntities?,
            subRace = args[7] as DnDEntityMin?,
            background = args[8] as DnDEntityWithSubEntities?,
            subBackground = args[9] as DnDEntityMin?
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

    fun setCharacterClass(value: DnDEntityWithSubEntities?, subClass: DnDEntityMin? = null) {
        _characterCls.value = value
        _characterSubCls.value = subClass
    }
    fun setCharacterSubClass(value: DnDEntityMin?) { _characterSubCls.value = value }

    fun setCharacterRace(value: DnDEntityWithSubEntities?, subRace: DnDEntityMin? = null) {
        _characterRace.value = value
        _characterSubRace.value = subRace
    }
    fun setCharacterSubRace(value: DnDEntityMin?) { _characterSubRace.value = value }

    fun setCharacterBackground(value: DnDEntityWithSubEntities?) { _characterBackground.value = value }
    fun setCharacterSubBackground(value: DnDEntityMin?) { _characterSubBackground.value = value }

    // downloadable items

    private val _mainClasses = MutableStateFlow<List<DnDEntityWithSubEntities>>(emptyList())
    private val _mainRaces = MutableStateFlow<List<DnDEntityWithSubEntities>>(emptyList())
    private val _mainBackgrounds = MutableStateFlow<List<DnDEntityWithSubEntities>>(emptyList())

    init {
        loadMainValues()
    }

    private fun loadMainValues() = viewModelScope.launch {
        _mainClasses.value = repository.getEntitiesWithSubList(DnDEntityTypes.CLASS)
        _mainRaces.value = repository.getEntitiesWithSubList(DnDEntityTypes.RACE)
        _mainBackgrounds.value = repository.getEntitiesWithSubList(DnDEntityTypes.BACKGROUND)
    }

    val downloadableState: StateFlow<DownloadableValuesState> = combine(
        _mainClasses, _mainRaces, _mainBackgrounds
    ) { classes, races, backgrounds ->
        DownloadableValuesState(
            classes = classes,
            races = races,
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
                setSearchQuery(_searchQuery.value)
            }
            DnDEntityTypes.RACE -> {
                val entities = getEntities()
                _searchRaceEntities.value = entities
                setSearchQuery(_searchQuery.value)
            }
            DnDEntityTypes.BACKGROUND -> {
                val entities = getEntities()
                _searchBackgroundEntities.value = entities
                setSearchQuery(_searchQuery.value)
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
    val classes: List<DnDEntityWithSubEntities> = emptyList(),
    val races: List<DnDEntityWithSubEntities> = emptyList(),
    val backgrounds: List<DnDEntityWithSubEntities> = emptyList()
)

data class NewCharacterState(
    val images: List<Path> = emptyList(),
    val mainImage: Path? = null,
    val name: String = "",
    val description: String = "",
    val cls: DnDEntityWithSubEntities? = null,
    val subCls: DnDEntityMin? = null,
    val race: DnDEntityWithSubEntities? = null,
    val subRace: DnDEntityMin? = null,
    val background: DnDEntityWithSubEntities? = null,
    val subBackground: DnDEntityMin? = null,
)

data class SearchSheetUiState(
    val isLoading: Boolean = false,
    val searchType: DnDEntityTypes = DnDEntityTypes.NONE,
    val query: String = "",
    val entitiesGroups: Map<String, List<DnDEntityWithSubEntities>> = emptyMap()
)
