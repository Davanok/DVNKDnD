package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.ui.UiError
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.searchSheet.SearchSheetViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_entities_error
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class NewCharacterMainViewModel(
    private val filesRepository: FilesRepository,
    private val entitiesRepository: EntitiesRepository,
    val searchSheetViewModel: SearchSheetViewModel,
    private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewCharacterMainUiState(isLoading = true))
    val uiState: StateFlow<NewCharacterMainUiState> = _uiState

    fun removeWarning() {
        _uiState.update { it.copy(error = null) }
    }

    private suspend fun setCriticalError(messageRes: StringResource, thr: Throwable?) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = UiError.Critical(
                    message = getString(messageRes),
                    exception = thr
                )
            )
        }
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

        val missingIds = listOfNotNull(
            character.cls?.id?.takeIf { characterClass == null },
            character.race?.id?.takeIf { characterRace == null },
            character.background?.id?.takeIf { characterBackground == null }
        )

        if (missingIds.isNotEmpty()) {
            entitiesRepository.getEntitiesWithSubList(missingIds)
                .onFailure { thr ->
                    setCriticalError(Res.string.loading_entities_error, thr)
                    return
                }
                .onSuccess { loaded ->
                    val byId = loaded.associateBy { it.id }
                    character.cls?.id?.let { characterClass = characterClass ?: byId[it] }
                    character.race?.id?.let { characterRace = characterRace ?: byId[it] }
                    character.background?.id?.let { characterBackground = characterBackground ?: byId[it] }
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
        _uiState.update { it.copy(isLoading = true, error = null) }

        entitiesRepository.getEntitiesWithSubList(
            DnDEntityTypes.CLASS,
            DnDEntityTypes.RACE,
            DnDEntityTypes.BACKGROUND
        ).onFailure { thr ->
            setCriticalError(Res.string.loading_entities_error, thr)
        }.onSuccess { entities ->
            val values = DownloadableValues(
                classes = entities[DnDEntityTypes.CLASS] ?: emptyList(),
                races = entities[DnDEntityTypes.RACE] ?: emptyList(),
                backgrounds = entities[DnDEntityTypes.BACKGROUND] ?: emptyList()
            )

            _uiState.update { it.copy(entities = values) }

            setCharacterEntities()

            _uiState.update { it.copy(isLoading = false) }
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
                DnDEntityTypes.BACKGROUND -> setCharacterBackground(selectedEntity, selectedSubEntity)
                else -> throw IllegalArgumentException()
            }
        }
        _uiState.update { it.copy(showSearchSheet = false) }
    }

    fun openSearchSheet(content: DnDEntityTypes, query: String) {
        _uiState.update { it.copy(showSearchSheet = true) }
        searchSheetViewModel.setSearchType(content)
        searchSheetViewModel.setSearchQuery(query)
    }

    private fun updateCharacter(update: (NewCharacterMain) -> NewCharacterMain) {
        _uiState.update { it.copy(character = update(it.character)) }
    }

    fun addCharacterImage(value: ByteArray) = viewModelScope.launch {
        val path = filesRepository.getFilename(FilesRepository.Paths.images, "png", true)
        filesRepository.write(value, path)
            .onSuccess {
                updateCharacter {
                    val mutable = it.images.toMutableList()
                    mutable.add(0, path)
                    it.copy(images = mutable, mainImage = path)
                }
            }.onFailure { thr ->
                setCriticalError(Res.string.saving_data_error, thr)
            }
    }

    fun removeCharacterImage(value: Path) = viewModelScope.launch {
        filesRepository.delete(value)
            .onFailure { thr ->
                _uiState.update {
                    it.copy(error = UiError.Critical(getString(Res.string.saving_data_error), thr))
                }
            }.also {
                updateCharacter {
                    val images = it.images.fastFilter { path -> path != value }
                    val wasMainImage = it.mainImage == value
                    it.copy(
                        images = images,
                        mainImage = if (wasMainImage) images.firstOrNull() else it.mainImage
                    )
                }
            }
    }

    fun setCharacterMainImage(value: Path?) = updateCharacter { it.copy(mainImage = value) }
    fun setCharacterName(value: String) = updateCharacter { it.copy(name = value) }
    fun setCharacterDescription(value: String) = updateCharacter { it.copy(description = value) }
    fun setCharacterClass(cls: DnDEntityWithSubEntities?, subCls: DnDEntityMin? = null) =
        updateCharacter { it.copy(cls = cls, subCls = subCls) }
    fun setCharacterSubClass(value: DnDEntityMin?) = updateCharacter { it.copy(subCls = value) }
    fun setCharacterRace(race: DnDEntityWithSubEntities?, subRace: DnDEntityMin? = null) =
        updateCharacter { it.copy(race = race, subRace = subRace) }
    fun setCharacterSubRace(value: DnDEntityMin?) = updateCharacter { it.copy(subRace = value) }
    fun setCharacterBackground(
        background: DnDEntityWithSubEntities?,
        subBackground: DnDEntityMin? = null,
    ) = updateCharacter { it.copy(background = background, subBackground = subBackground) }
    fun setCharacterSubBackground(value: DnDEntityMin?) = updateCharacter { it.copy(subBackground = value) }

    private fun validateCharacter(character: NewCharacterMain): NewCharacterMainUiState.EmptyFields {
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

        val emptyFields = validateCharacter(newCharacter)

        if (emptyFields.hasEmptyFields())
            _uiState.update { it.copy(emptyFields = emptyFields) }
        else {
            _uiState.update { it.copy(isLoading = true, error = null) }
            newCharacterViewModel.setCharacterMain(newCharacter)
                .onFailure { thr ->
                    setCriticalError(Res.string.saving_data_error, thr)
                }.onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
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
