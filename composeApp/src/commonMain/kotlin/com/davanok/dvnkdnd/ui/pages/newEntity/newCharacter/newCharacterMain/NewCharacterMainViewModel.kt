package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.local.EntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.local.FilesRepository
import com.davanok.dvnkdnd.domain.values.FilePaths
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterMain
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.NewCharacterViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.loading_entities_error
import dvnkdnd.composeapp.generated.resources.saving_data_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@AssistedInject
class NewCharacterMainViewModel(
    private val filesRepository: FilesRepository,
    private val entitiesRepository: EntitiesRepository,
    @Assisted private val newCharacterViewModel: NewCharacterViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewCharacterMainUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadMainValues()
    }

    // region Loading & Initialization
    private fun loadMainValues() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }

        entitiesRepository.getEntitiesWithSubList(
            DnDEntityTypes.CLASS,
            DnDEntityTypes.RACE,
            DnDEntityTypes.BACKGROUND
        ).onFailure { t ->
            setCriticalError(Res.string.loading_entities_error, t)
        }.onSuccess { entities ->
            val values = DownloadableValues(
                classes = entities[DnDEntityTypes.CLASS].orEmpty(),
                races = entities[DnDEntityTypes.RACE].orEmpty(),
                backgrounds = entities[DnDEntityTypes.BACKGROUND].orEmpty()
            )

            _uiState.update { it.copy(entities = values) }
            setCharacterEntities()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun setCharacterEntities() {
        val entities = _uiState.value.entities
        val char = newCharacterViewModel.getCharacterMain()

        // Helper to resolve an entity from the downloaded lists or mark for fetching
        fun resolve(current: DnDEntityWithSubEntities?, list: List<DnDEntityWithSubEntities>): DnDEntityWithSubEntities? {
            return current?.let { entity -> list.fastFirstOrNull { it.id == entity.id } }
        }

        var cls = resolve(char.cls, entities.classes)
        var race = resolve(char.race, entities.races)
        var bg = resolve(char.background, entities.backgrounds)

        // Identify IDs that weren't found in the pre-loaded lists
        val missingIds = listOfNotNull(
            char.cls?.id.takeIf { cls == null },
            char.race?.id.takeIf { race == null },
            char.background?.id.takeIf { bg == null }
        )

        if (missingIds.isNotEmpty()) {
            entitiesRepository.getEntitiesWithSubList(missingIds)
                .onFailure { t -> setCriticalError(Res.string.loading_entities_error, t); return }
                .onSuccess { loaded ->
                    val loadedMap = loaded.associateBy { it.id }
                    // Fallback to loaded map if still null, or keep original if absolutely nothing found
                    cls = cls ?: loadedMap[char.cls?.id]
                    race = race ?: loadedMap[char.race?.id]
                    bg = bg ?: loadedMap[char.background?.id]
                }
        }

        updateCharacter {
            it.copy(
                cls = cls ?: it.cls,
                race = race ?: it.race,
                background = bg ?: it.background
            )
        }
    }
    // endregion

    // region User Actions
    fun removeWarning() {
        _uiState.update { it.copy(error = null) }
    }

    fun openSearchSheet(content: DnDEntityTypes, query: String) {
        _uiState.update { it.copy(showSearchSheet = content) }
    }

    fun hideSearchSheet() {
        _uiState.update { it.copy(showSearchSheet = null) }
    }

    fun selectSearchSheetEntity(
        selectedEntity: DnDEntityWithSubEntities?,
        selectedSubEntity: DnDEntityMin?,
    ) {
        selectedEntity?.type?.let { type ->
            when (type) {
                DnDEntityTypes.CLASS -> setCharacterClass(selectedEntity, selectedSubEntity)
                DnDEntityTypes.RACE -> setCharacterRace(selectedEntity, selectedSubEntity)
                DnDEntityTypes.BACKGROUND -> setCharacterBackground(selectedEntity, selectedSubEntity)
                else -> throw IllegalArgumentException("Invalid entity type: $type")
            }
        }
        hideSearchSheet()
    }

    fun commit(onSuccess: () -> Unit) = viewModelScope.launch {
        val newCharacter = _uiState.value.character
        val emptyFields = validateCharacter(newCharacter)

        if (emptyFields.hasEmptyFields()) {
            _uiState.update { it.copy(isNextButtonLoading = false, emptyFields = emptyFields) }
        } else {
            _uiState.update { it.copy(isNextButtonLoading = true) } // Ensure loading state is visible

            newCharacterViewModel.setCharacterMain(newCharacter)
                .onFailure { t ->
                    _uiState.update { it.copy(isNextButtonLoading = false) }
                    setCriticalError(Res.string.saving_data_error, t)
                }.onSuccess {
                    _uiState.update { it.copy(isNextButtonLoading = false) }
                    onSuccess()
                }
        }
    }
    // endregion

    // region Image Handling
    fun addCharacterImage(value: ByteArray) = viewModelScope.launch {
        val path = filesRepository.getFilename(FilePaths.images, "png", true)
        filesRepository.write(value, path)
            .onSuccess {
                updateCharacter { char ->
                    val mutableImages = char.images.toMutableList().apply { add(0, path) }
                    char.copy(images = mutableImages, mainImage = path)
                }
            }.onFailure { t ->
                setCriticalError(Res.string.saving_data_error, t)
            }
    }

    fun removeCharacterImage(value: Path) = viewModelScope.launch {
        filesRepository.delete(value).onFailure { t ->
            setCriticalError(Res.string.saving_data_error, t)
        }

        updateCharacter { char ->
            val images = char.images.fastFilter { it != value }
            val wasMainImage = char.mainImage == value
            char.copy(
                images = images,
                mainImage = if (wasMainImage) images.firstOrNull() else char.mainImage
            )
        }
    }
    // endregion

    // region Setters
    fun setCharacterClass(cls: DnDEntityWithSubEntities?, subCls: DnDEntityMin? = null) =
        updateCharacter { it.copy(cls = cls, subCls = subCls) }

    fun setCharacterRace(race: DnDEntityWithSubEntities?, subRace: DnDEntityMin? = null) =
        updateCharacter { it.copy(race = race, subRace = subRace) }

    fun setCharacterBackground(bg: DnDEntityWithSubEntities?, subBg: DnDEntityMin? = null) =
        updateCharacter { it.copy(background = bg, subBackground = subBg) }

    fun evenSink(event: NewCharacterMainEvent) = when (event) {
        is NewCharacterMainEvent.AddImage ->
            addCharacterImage(event.bytes)
        is NewCharacterMainEvent.RemoveImage ->
            removeCharacterImage(event.path)
        is NewCharacterMainEvent.SetMainImage ->
            updateCharacter { it.copy(mainImage = event.path) }

        is NewCharacterMainEvent.SetName ->
            updateCharacter { it.copy(name = event.name) }

        is NewCharacterMainEvent.SetDescription ->
            updateCharacter { it.copy(description = event.description) }

        is NewCharacterMainEvent.SetClass ->
            updateCharacter {
                it.copy(
                    cls = event.cls,
                    subCls = event.subCls
                )
            }

        is NewCharacterMainEvent.SetSubClass ->
            updateCharacter { it.copy(subCls = event.subCls) }

        is NewCharacterMainEvent.SetRace ->
            updateCharacter {
                it.copy(
                    race = event.race,
                    subRace = event.subRace
                )
            }

        is NewCharacterMainEvent.SetSubRace ->
            updateCharacter { it.copy(subRace = event.subRace) }

        is NewCharacterMainEvent.SetBackground ->
            updateCharacter {
                it.copy(
                    background = event.background,
                    subBackground = event.subBackground
                )
            }

        is NewCharacterMainEvent.SetSubBackground ->
            updateCharacter { it.copy(subBackground = event.subBackground) }

        is NewCharacterMainEvent.OpenSearchSheet ->
            openSearchSheet(event.entityType, event.query)
    }
    // endregion

    // region Helpers
    private fun updateCharacter(update: (NewCharacterMain) -> NewCharacterMain) {
        _uiState.update { it.copy(character = update(it.character)) }
    }

    private suspend fun setCriticalError(messageRes: StringResource, t: Throwable?) {
        val message = getString(messageRes)
        _uiState.update {
            it.copy(
                isLoading = false,
                error = UiError.Critical(message = message, exception = t)
            )
        }
    }

    private fun validateCharacter(character: NewCharacterMain): NewCharacterMainUiState.EmptyFields {
        fun isMissingSub(entity: DnDEntityWithSubEntities?, sub: DnDEntityMin?): Boolean {
            return entity != null && entity.subEntities.isNotEmpty() && (sub == null || sub !in entity.subEntities)
        }

        return NewCharacterMainUiState.EmptyFields(
            name = character.name.isBlank(),
            cls = character.cls == null,
            subCls = isMissingSub(character.cls, character.subCls),
            race = character.race == null,
            subRace = isMissingSub(character.race, character.subRace),
            background = character.background == null,
            subBackground = isMissingSub(character.background, character.subBackground)
        )
    }
    // endregion

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey(Factory::class)
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted newCharacterViewModel: NewCharacterViewModel): NewCharacterMainViewModel
    }
}

// region Data Classes
data class NewCharacterMainUiState(
    val isLoading: Boolean = false,
    val isNextButtonLoading: Boolean = false,
    val error: UiError? = null,
    val showSearchSheet: DnDEntityTypes? = null,
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
        fun hasEmptyFields(): Boolean = listOf(
            name, cls, subCls, race, subRace, background, subBackground
        ).fastAny { it }
    }
}

data class DownloadableValues(
    val classes: List<DnDEntityWithSubEntities> = emptyList(),
    val races: List<DnDEntityWithSubEntities> = emptyList(),
    val backgrounds: List<DnDEntityWithSubEntities> = emptyList(),
)
// endregion