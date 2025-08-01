package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain


import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_entities_error
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import kotlin.uuid.Uuid

class NewCharacterMainViewModel(
    private val filesRepository: FilesRepository,
    private val browseRepository: BrowseRepository,
    private val entitiesRepository: EntitiesRepository,
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        NewCharacterMainUiState(isLoading = true)
    )
    val uiState: StateFlow<NewCharacterMainUiState> = _uiState

    fun removeWarning() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private suspend fun setCharacterEntities() {
        val values = _uiState.value.entities
        val character = newCharacterViewModel.getCharacterMain()

        var characterClass = character.cls?.let { cls ->
            values.classes.fastFirstOrNull { it.id == cls.id }
        }
        var characterRace = character.race?.let { race ->
            values.races.fastFirstOrNull { it.id == race.id }
        }
        var characterBackground = character.background?.let { background ->
            values.backgrounds.fastFirstOrNull { it.id == background.id }
        }

        val entitiesToLoad = mutableListOf<Uuid>()

        if (characterClass == null && character.cls != null) entitiesToLoad.add(character.cls.id)
        if (characterRace == null && character.race != null) entitiesToLoad.add(character.race.id)
        if (characterBackground == null && character.background != null) entitiesToLoad.add(character.background.id)

        if (entitiesToLoad.isNotEmpty()) {
            entitiesRepository.getEntitiesWithSubList(entitiesToLoad).onFailure { thr ->
                _uiState.update {
                    it.copy(
                        error = UiError.Critical(Res.string.loading_entities_error, thr)
                    )
                }
            }.onSuccess { loaded ->
                loaded.fastForEach {
                    when (it.id) {
                        character.cls?.id -> characterClass = it
                        character.race?.id -> characterRace = it
                        character.background?.id -> characterBackground = it
                    }
                }
            }
        }
        _uiState.update {
            it.copy(
                character = character.copy(
                    cls = characterClass,
                    race = characterRace,
                    background = characterBackground
                )
            )
        }
    }

    private fun loadMainValues() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        entitiesRepository.getEntitiesWithSubList(
            DnDEntityTypes.CLASS,
            DnDEntityTypes.RACE,
            DnDEntityTypes.BACKGROUND
        ).onFailure { thr ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = UiError.Critical(Res.string.loading_entities_error, thr)
                )
            }
        }.onSuccess { entities ->
            val values = DownloadableValues(
                classes = entities[DnDEntityTypes.CLASS] ?: emptyList(),
                races = entities[DnDEntityTypes.RACE] ?: emptyList(),
                backgrounds = entities[DnDEntityTypes.BACKGROUND] ?: emptyList()
            )
            _uiState.update {
                it.copy(entities = values)
            }
            setCharacterEntities()
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

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
    private fun updateCharacter(update: (NewCharacterMain) -> NewCharacterMain) {
        val character = _uiState.value.character
        val updated = update(character)
        _uiState.update {
            it.copy(
                character = updated
            )
        }
    }

    fun addCharacterImage(value: ByteArray) = viewModelScope.launch {
        val path = filesRepository.getFilename(
            FilesRepository.Paths.images,
            "png",
            true
        ) // png as default in ImageBitmap.toByteArray
        filesRepository.write(value, path)

        updateCharacter {
            val mutable = it.images.toMutableList()
            mutable.add(0, path)
            it.copy(
                images = mutable,
                mainImage = path
            )
        }
    }

    fun removeCharacterImage(value: Path) = viewModelScope.launch {
        filesRepository.delete(value)

        updateCharacter {
            val images = it.images.fastFilter { path -> path != value }
            val wasMainImage = it.mainImage == value
            it.copy(
                images = images,
                mainImage = if (wasMainImage) images.firstOrNull() else it.mainImage
            )
        }
    }

    fun setCharacterMainImage(value: Path?) =
        updateCharacter { it.copy(mainImage = value) }

    fun setCharacterName(value: String) =
        updateCharacter { it.copy(name = value) }

    fun setCharacterDescription(value: String) =
        updateCharacter { it.copy(description = value) }

    fun setCharacterClass(cls: DnDEntityWithSubEntities?, subCls: DnDEntityMin? = null) =
        updateCharacter { it.copy(cls = cls, subCls = subCls) }

    fun setCharacterSubClass(value: DnDEntityMin?) =
        updateCharacter { it.copy(subCls = value) }

    fun setCharacterRace(race: DnDEntityWithSubEntities?, subRace: DnDEntityMin? = null) =
        updateCharacter { it.copy(race = race, subRace = subRace) }

    fun setCharacterSubRace(value: DnDEntityMin?) =
        updateCharacter { it.copy(subRace = value) }

    fun setCharacterBackground(
        background: DnDEntityWithSubEntities?,
        subBackground: DnDEntityMin? = null,
    ) =
        updateCharacter { it.copy(background = background, subBackground = subBackground) }

    fun setCharacterSubBackground(value: DnDEntityMin?) =
        updateCharacter { it.copy(subBackground = value) }

    // Search content
    private val _searchSheetState = MutableStateFlow(
        SearchSheetUiState(
            isLoading = true,
            searchType = DnDEntityTypes.CLASS
        )
    )
    val searchSheetState: StateFlow<SearchSheetUiState> = _searchSheetState
    private val loadedEntities = mutableMapOf(
        DnDEntityTypes.CLASS to emptyMap(),
        DnDEntityTypes.RACE to emptyMap(),
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

        _searchSheetState.value = _searchSheetState.value.copy(entitiesGroups = filteredEntities)
    }

    fun loadSearchEntities(content: DnDEntityTypes) = viewModelScope.launch {
        _searchSheetState.value = _searchSheetState.value.copy(isLoading = true)

        browseRepository.loadEntitiesWithSub(content).onFailure { thr ->
            _uiState.update {
                it.copy(
                    error = UiError.Critical(Res.string.loading_entities_error, thr)
                )
            }
        }.onSuccess { entities ->
            loadedEntities[content] = entities.groupBy { it.source }
        }

        _searchSheetState.value = _searchSheetState.value.copy(isLoading = false)
        filterEntities()
    }

    fun setSearchQuery(value: String) {
        _searchSheetState.value = _searchSheetState.value.copy(query = value)
        filterEntities()
    }

    // onCreate

    private fun checkCharacter(character: NewCharacterMain): NewCharacterMainUiState.EmptyFields {
        var (
            name,
            cls,
            subCls,
            race,
            subRace,
            background,
            subBackground,
        ) = NewCharacterMainUiState.EmptyFields()

        if (character.name.isBlank()) name = true
        if (character.cls == null) cls = true
        else if (character.cls.subEntities.isNotEmpty() && character.subCls !in character.cls.subEntities)
            subCls = true
        if (character.race == null) race = true
        else if (character.race.subEntities.isNotEmpty() && character.subRace !in character.race.subEntities)
            subRace = true
        if (character.background == null) background = true
        else if (character.background.subEntities.isNotEmpty() && character.subBackground !in character.background.subEntities)
            subBackground = true

        return NewCharacterMainUiState.EmptyFields(
            name = name,
            cls = cls,
            subCls = subCls,
            race = race,
            subRace = subRace,
            background = background,
            subBackground = subBackground
        )
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        val newCharacter = _uiState.value.character

        val emptyFields = checkCharacter(newCharacter)

        if (emptyFields.hasEmptyFields())
            _uiState.update { it.copy(emptyFields = emptyFields) }
        else {
            _uiState.update {
                it.copy(isLoading = true)
            }
            newCharacterViewModel.setCharacterMain(newCharacter).onFailure { thr ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = UiError.Critical(
                            Res.string.saving_data_error,
                            thr
                        )
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                onSuccess()
            }
        }
    }
    init {
        loadMainValues()
    }
}

data class NewCharacterMainUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val showSearchSheet: Boolean = false,
    val emptyFields: EmptyFields = EmptyFields(),
    val entities: DownloadableValues = DownloadableValues(),
    val character: NewCharacterMain = NewCharacterMain()
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
        fun hasEmptyFields(): Boolean {
            val values = listOf(
                name, cls, subCls, race, subRace, background, subBackground
            )
            return values.fastAny { it }
        }
    }
}

data class DownloadableValues(
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
    fun getEntitiesIds() = listOfNotNull(
        cls?.id,
        subCls?.id,
        race?.id,
        subRace?.id,
        background?.id,
        subBackground?.id
    )
}

data class SearchSheetUiState(
    val isLoading: Boolean = false,
    val searchType: DnDEntityTypes,
    val query: String = "",
    val entitiesGroups: Map<String, List<DnDEntityWithSubEntities>> = emptyMap(),
)
