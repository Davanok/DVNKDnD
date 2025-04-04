package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.Path
import kotlin.uuid.ExperimentalUuidApi

class NewCharacterMainViewModel(
    private val repository: NewCharacterRepository,
    private val filesRepository: FilesRepository,
    private val browseRepository: BrowseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCharacterUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterUiState> = _uiState

    fun hideSearchSheet(
        selectedEntity: DnDEntityWithSubEntities?,
        selectedSubEntity: DnDEntityMin?,
    ) {
        selectedEntity?.type?.let {
            when (it) {
                DnDEntityTypes.CLASS -> setCharacterClass(selectedEntity, selectedSubEntity)
                DnDEntityTypes.RACE -> setCharacterRace(selectedEntity, selectedSubEntity)
                DnDEntityTypes.BACKGROUND -> setCharacterBackground(
                    selectedEntity,
                    selectedSubEntity
                )

                else -> throw IllegalArgumentException()
            }
        }
        _uiState.value = _uiState.value.copy(showSearchSheet = false)
    }

    fun openSearchSheet(content: DnDEntityTypes, query: String) {
        _uiState.value = _uiState.value.copy(showSearchSheet = true)
        _searchSheetState.value = _searchSheetState.value.copy(
            searchType = content
        )
        setSearchQuery(query)

        val needLoad = loadedEntities[content].isNullOrEmpty()
        if (needLoad) loadSearchEntities(content)
    }

    // Character setters

    private val _newCharacterMain = MutableStateFlow(NewCharacterMain())
    val newCharacterMain: StateFlow<NewCharacterMain> = _newCharacterMain


    @OptIn(ExperimentalUuidApi::class)
    fun addCharacterImage(value: ByteArray) = viewModelScope.launch {
        val path = filesRepository.getFilename(
            FilesRepository.Paths.images,
            "png",
            true
        ) // png as default in ImageBitmap.toByteArray
        filesRepository.write(value, path)
        val mutable = _newCharacterMain.value.images.toMutableList()
        mutable.add(0, path)

        _newCharacterMain.value = _newCharacterMain.value.copy(
            images = mutable.toList(),
            mainImage = path
        )
    }

    fun removeCharacterImage(value: Path) = viewModelScope.launch {
        filesRepository.delete(value)
        val images = newCharacterMain.value.images

        _newCharacterMain.value = newCharacterMain.value.copy(
            images = images.fastFilter { it != value },
            mainImage = images.firstOrNull()
        )
    }

    fun setCharacterMainImage(value: Path?) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            mainImage = value
        )
    }

    fun setCharacterName(value: String) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            name = value
        )
    }

    fun setCharacterDescription(value: String) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            description = value
        )
    }

    fun setCharacterClass(cls: DnDEntityWithSubEntities?, subCls: DnDEntityMin? = null) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            cls = cls,
            subCls = subCls
        )
    }

    fun setCharacterSubClass(value: DnDEntityMin?) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            subCls = value
        )
    }

    fun setCharacterRace(race: DnDEntityWithSubEntities?, subRace: DnDEntityMin? = null) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            race = race,
            subRace = subRace
        )
    }

    fun setCharacterSubRace(value: DnDEntityMin?) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            subRace = value
        )
    }

    fun setCharacterBackground(
        background: DnDEntityWithSubEntities?,
        subBackground: DnDEntityMin? = null,
    ) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            background = background,
            subBackground = subBackground
        )
    }

    fun setCharacterSubBackground(value: DnDEntityMin?) {
        _newCharacterMain.value = _newCharacterMain.value.copy(
            subBackground = value
        )
    }

    // downloadable items
    private val _downloadableState = MutableStateFlow(DownloadableValuesState(isLoading = true))
    val downloadableState: StateFlow<DownloadableValuesState> = _downloadableState

    private fun loadMainValues() = viewModelScope.launch {
        val classes = repository.getEntitiesWithSubList(DnDEntityTypes.CLASS)
        val races = repository.getEntitiesWithSubList(DnDEntityTypes.RACE)
        val backgrounds = repository.getEntitiesWithSubList(DnDEntityTypes.BACKGROUND)

        _downloadableState.value = _downloadableState.value.copy(
            isLoading = false,
            classes = classes,
            races = races,
            backgrounds = backgrounds
        )
    }

    // Search content
    private val _searchSheetState = MutableStateFlow(
        SearchSheetUiState(
            isLoading = true,
            searchType = DnDEntityTypes.CLASS
        )
    )
    val searchSheetState: StateFlow<SearchSheetUiState> = _searchSheetState

    private val loadedEntities = mutableMapOf(
        DnDEntityTypes.CLASS to emptyMap<String, List<DnDEntityWithSubEntities>>(),
        DnDEntityTypes.RACE to emptyMap<String, List<DnDEntityWithSubEntities>>(),
        DnDEntityTypes.BACKGROUND to emptyMap<String, List<DnDEntityWithSubEntities>>()
    )

    private fun filterEntities() {
        val query = _searchSheetState.value.query
        val content = _searchSheetState.value.searchType
        val entities = loadedEntities[content]
            ?: throw IllegalArgumentException("Illegal to search entities of $content type in search sheet")

        val filteredEntities =
            if (query.isEmpty()) entities
            else entities.mapValues { (_, list) ->
                list.fastFilter { it.name.startsWith(query, ignoreCase = true) }
            }.filterValues { it.isNotEmpty() }

        _searchSheetState.value = _searchSheetState.value.copy(
            entitiesGroups = filteredEntities
        )
    }

    fun loadSearchEntities(content: DnDEntityTypes) = viewModelScope.launch {
        _searchSheetState.value = _searchSheetState.value.copy(isLoading = true)

        loadedEntities[content] = browseRepository
            .loadEntities(content)
            .groupBy { it.source }

        _searchSheetState.value = _searchSheetState.value.copy(isLoading = false)
        filterEntities()
    }

    fun setSearchQuery(value: String) {
        _searchSheetState.value = _searchSheetState.value.copy(query = value)
        filterEntities()
    }

    // onCreate

    private fun checkCharacter(character: NewCharacterMain): NewCharacterUiState.EmptyFields {
        var (
            name, cls, subCls, race, subRace, background, subBackground,
        ) = NewCharacterUiState.EmptyFields()

        if (character.name.isBlank()) name = true
        if (character.cls == null) cls = true
        else if (character.subCls !in character.cls.subEntities) subCls = true
        if (character.race == null) race = true
        else if (character.subRace !in character.race.subEntities) subRace = true
        if (character.background == null) background = true
        else if (character.subBackground !in character.background.subEntities) subBackground = true

        return NewCharacterUiState.EmptyFields(
            name = name,
            cls = cls,
            subCls = subCls,
            race = race,
            subRace = subRace,
            background = background,
            subBackground = subBackground
        )
    }

    fun createCharacter(onSuccess: (characterId: Long) -> Unit) = viewModelScope.launch {
        // TODO: save images
        // TODO: save class / race / background to database
        val newCharacter = newCharacterMain.value

        val emptyFields = checkCharacter(newCharacter)

        if (emptyFields.isEmpty()) _uiState.value = _uiState.value.copy(emptyFields = emptyFields)
        else {
            val characterId = repository.createCharacter(newCharacter.getCharacter())
            onSuccess(characterId)
        }
    }

    // init
    init {
        loadMainValues()
    }
}

data class NewCharacterUiState(
    val isLoading: Boolean = false,
    val showSearchSheet: Boolean = false,
    val emptyFields: EmptyFields = EmptyFields(),
) {
    data class EmptyFields(
        val name: Boolean = false,
        val cls: Boolean = false,
        val subCls: Boolean = false,
        val race: Boolean = false,
        val subRace: Boolean = false,
        val background: Boolean = false,
        val subBackground: Boolean = false,
    ) {
        fun isEmpty(): Boolean {
            val values = listOf(
                name, cls, subCls, race, subRace, background, subBackground
            )
            return values.any { it }
        }
    }
}

data class DownloadableValuesState(
    val isLoading: Boolean = false,
    val classes: List<DnDEntityWithSubEntities> = emptyList(),
    val races: List<DnDEntityWithSubEntities> = emptyList(),
    val backgrounds: List<DnDEntityWithSubEntities> = emptyList(),
)

data class NewCharacterMain(
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
) {
    fun getCharacter() = Character(
        name = name,
        description = description,
        cls = cls?.id,
        subCls = subCls?.id,
        race = race?.id,
        subRace = subRace?.id,
        background = background?.id,
        subBackground = subBackground?.id
    )
}

data class SearchSheetUiState(
    val isLoading: Boolean = false,
    val searchType: DnDEntityTypes,
    val query: String = "",
    val entitiesGroups: Map<String, List<DnDEntityWithSubEntities>> = emptyMap(),
)
