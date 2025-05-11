package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.ui.components.UiMessage
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.error_when_loading_entity
import dvnkdnd.composeapp.generated.resources.finish
import dvnkdnd.composeapp.generated.resources.loading
import dvnkdnd.composeapp.generated.resources.state_checking_data
import dvnkdnd.composeapp.generated.resources.state_downloading
import dvnkdnd.composeapp.generated.resources.state_loading
import dvnkdnd.composeapp.generated.resources.state_loading_from_database
import dvnkdnd.composeapp.generated.resources.state_loading_full_entities
import dvnkdnd.composeapp.generated.resources.state_updating_entities
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.Path
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

class NewCharacterMainViewModel(
    private val charactersRepository: CharactersRepository,
    private val entitiesRepository: EntitiesRepository,
    private val filesRepository: FilesRepository,
    private val browseRepository: BrowseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCharacterMainUiState())
    val uiState: StateFlow<NewCharacterMainUiState> = _uiState

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
        _newCharacterMain.value = _newCharacterMain.value.copy(mainImage = value)
    }

    fun setCharacterName(value: String) {
        _newCharacterMain.value = _newCharacterMain.value.copy(name = value)
    }

    fun setCharacterDescription(value: String) {
        _newCharacterMain.value = _newCharacterMain.value.copy(description = value)
    }

    fun setCharacterClass(cls: DnDEntityWithSubEntities?, subCls: DnDEntityMin? = null) {
        _newCharacterMain.value = _newCharacterMain.value.copy(cls = cls, subCls = subCls)
    }

    fun setCharacterSubClass(value: DnDEntityMin?) {
        _newCharacterMain.value = _newCharacterMain.value.copy(subCls = value)
    }

    fun setCharacterRace(race: DnDEntityWithSubEntities?, subRace: DnDEntityMin? = null) {
        _newCharacterMain.value = _newCharacterMain.value.copy(race = race, subRace = subRace)
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
        _newCharacterMain.value =
            _newCharacterMain.value.copy(background = background, subBackground = subBackground)
    }

    fun setCharacterSubBackground(value: DnDEntityMin?) {
        _newCharacterMain.value = _newCharacterMain.value.copy(subBackground = value)
    }

    // downloadable items
    private val _downloadableState = MutableStateFlow(DownloadableValuesState(isLoading = true))
    val downloadableState: StateFlow<DownloadableValuesState> = _downloadableState

    private fun loadMainValues() = viewModelScope.launch {
        val classes = entitiesRepository.getEntitiesWithSubList(DnDEntityTypes.CLASS)
        val races = entitiesRepository.getEntitiesWithSubList(DnDEntityTypes.RACE)
        val backgrounds = entitiesRepository.getEntitiesWithSubList(DnDEntityTypes.BACKGROUND)

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

        _searchSheetState.value = _searchSheetState.value.copy(entitiesGroups = filteredEntities)
    }

    fun loadSearchEntities(content: DnDEntityTypes) = viewModelScope.launch {
        _searchSheetState.value = _searchSheetState.value.copy(isLoading = true)

        runCatching {
            loadedEntities[content] = browseRepository
                .loadEntitiesWithSub(content)
                .groupBy { it.source }
        }.onFailure { thr ->
            addMessage(UiMessage.Error(getString(Res.string.error_when_loading_entity), error=thr))
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
            subBackground
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

    fun createCharacter(onSuccess: (characterId: Uuid) -> Unit) = viewModelScope.launch {
        val newCharacter = newCharacterMain.value

        val emptyFields = checkCharacter(newCharacter)

        if (emptyFields.hasEmptyFields()) _uiState.value = _uiState.value.copy(emptyFields = emptyFields)
        else {
            val character = newCharacter.getCharacter()
            val entities =
                listOfNotNull(
                    character.race,
                    character.subRace,
                    character.background,
                    character.subBackground,
                    newCharacter.cls?.id,
                    newCharacter.subCls?.id
                )
            loadRequiredEntities(entities) {
                val characterId = charactersRepository.createCharacter(character)
                newCharacter.images.forEach {
                    filesRepository.move(
                        it,
                        filesRepository.getFilename(
                            FilesRepository.Paths.characterImages / characterId.toString(),
                            "png"
                        )
                    )
                }
                onSuccess(characterId)
            }
        }
    }
    private fun addMessage(message: UiMessage) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + message
        )
    }
    fun removeMessage(messageId: Uuid) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages.fastFilter { it.id == messageId }
        )
    }
    private fun setCheckingState(state: NewCharacterMainUiState.CheckingDataStates, thr: Throwable? = null) = viewModelScope.launch {
        when (state) {
            NewCharacterMainUiState.CheckingDataStates.ERROR ->
                addMessage(UiMessage.Error(getString(state.text), error=thr))
            NewCharacterMainUiState.CheckingDataStates.FINISH ->
                addMessage(UiMessage.Success(getString(state.text)))
            else ->
                addMessage(UiMessage.Loading(getString(Res.string.loading)))
        }
        _uiState.value = _uiState.value.copy(checkingDataState = state)
    }

    private suspend fun loadRequiredEntities(requiredEntities: List<Uuid>, onSuccess: suspend () -> Unit) {
        runCatching {
            setCheckingState(NewCharacterMainUiState.CheckingDataStates.LOAD_FROM_DATABASE)
            val existingEntities = entitiesRepository.getExistingEntities(requiredEntities)

            setCheckingState(NewCharacterMainUiState.CheckingDataStates.CHECKING)
            val notExistingEntities = requiredEntities.subtract(existingEntities)

            if (notExistingEntities.isEmpty()) {
                setCheckingState(NewCharacterMainUiState.CheckingDataStates.FINISH)
                onSuccess()
                return
            }

            setCheckingState(NewCharacterMainUiState.CheckingDataStates.LOADING_DATA)
            val entities = browseRepository.loadEntitiesFullInfo(notExistingEntities.toList())

            setCheckingState(NewCharacterMainUiState.CheckingDataStates.UPDATING)
            entitiesRepository.insertFullEntities(entities)
        }.onFailure {
            Napier.e(throwable = it) { "error when load required entities" }
            setCheckingState(NewCharacterMainUiState.CheckingDataStates.ERROR, it)
        }.onSuccess {
            setCheckingState(NewCharacterMainUiState.CheckingDataStates.FINISH)
            onSuccess()
        }
    }

    // init

    private fun checkData(onSuccess: () -> Unit) = viewModelScope.launch {
        runCatching {
            setCheckingState(NewCharacterMainUiState.CheckingDataStates.LOAD_FROM_SERVER)

            Json.decodeFromString<List<Uuid>>(browseRepository.getValue("primary_base_entities"))
        }.onFailure {
            Napier.e(throwable = it) { "error when check data" }
            setCheckingState(NewCharacterMainUiState.CheckingDataStates.ERROR, it)
        }.onSuccess {
            loadRequiredEntities(it, onSuccess)
        }
    }

    init {
        checkData {
            loadMainValues()
        }
    }
}

data class NewCharacterMainUiState(
    val checkingDataState: CheckingDataStates = CheckingDataStates.LOADING,
    val showSearchSheet: Boolean = false,
    val emptyFields: EmptyFields = EmptyFields(),
    val messages: List<UiMessage> = emptyList()
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
            return values.any { it }
        }
    }

    enum class CheckingDataStates(val text: StringResource) {
        LOADING(Res.string.state_loading),
        LOAD_FROM_SERVER(Res.string.state_downloading),
        LOAD_FROM_DATABASE(Res.string.state_loading_from_database),
        CHECKING(Res.string.state_checking_data),
        LOADING_DATA(Res.string.state_loading_full_entities),
        UPDATING(Res.string.state_updating_entities),
        FINISH(Res.string.finish),
        ERROR(Res.string.error)
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
