package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.DnDEntityMin
import com.davanok.dvnkdnd.data.model.util.WhileUiSubscribed
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class NewCharacterViewModel(
    repository: NewCharacterRepository
) : ViewModel() {
    init {
        download(repository)
    }

    private val _message = MutableStateFlow<StringResource?>(null)
    private val _sheetContent = MutableStateFlow<SheetContent?>(null)

    val uiState: StateFlow<NewCharacterUiState> = combine(
        _message, _sheetContent
    ) { message, sheetContent ->
        NewCharacterUiState(
            isLoading = false,
            message = message,
            sheetContent = sheetContent
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NewCharacterUiState(isLoading = true)
    )
    fun hideSheet() {
        _sheetContent.value = null
    }

    // downloadable items

    private val _isDownloading = MutableStateFlow(true)
    private val _PHBClasses = MutableStateFlow<List<DnDEntityMin>>(emptyList())

    private fun download(repository: NewCharacterRepository) = viewModelScope.launch {
        _PHBClasses.value = repository.getClassesMinList("PHB")
    }.invokeOnCompletion {
        _isDownloading.value = false
    }

    val downloadableState: StateFlow<DownloadableValuesState> = combine(
        _isDownloading, _PHBClasses
    ) { isLoading, PHBClasses ->
        DownloadableValuesState(
            isLoading = isLoading,
            classes = PHBClasses
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = DownloadableValuesState()
    )

    // Character setters

    private val _characterImage = MutableStateFlow<PlatformFile?>(null)
    private val _characterName = MutableStateFlow("")
    private val _characterDescription = MutableStateFlow("")
    private val _characterCls = MutableStateFlow<DnDEntityMin?>(null)

    val newCharacterState: StateFlow<NewCharacterState> = combine(
        _characterImage, _characterName, _characterDescription, _characterCls
    ) { (image, name, description, cls) ->
        NewCharacterState(
            image = image as PlatformFile?,
            name = name as String,
            description = description as String,
            cls = cls as DnDEntityMin?
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NewCharacterState()
    )
    fun setCharacterImage(value: PlatformFile) { _characterImage.value = value }
    fun setCharacterName(value: String) { _characterName.value = value }
    fun setCharacterDescription(value: String) { _characterDescription.value = value }
    fun setCharacterClass(value: DnDEntityMin) { _characterCls.value = value }
}


enum class SheetContent {
    CLASS, RACE, BACKGROUND
}

data class NewCharacterUiState(
    val isLoading: Boolean = false,
    val message: StringResource? = null,
    val sheetContent: SheetContent? = null
)

data class DownloadableValuesState(
    val isLoading: Boolean = false,
    val classes: List<DnDEntityMin> = emptyList()
)

data class NewCharacterState(
    val image: PlatformFile? = null,
    val name: String = "",
    val description: String = "",
    val cls: DnDEntityMin? = null,
    val subCls: DnDEntityMin? = null,
    val race: DnDEntityMin? = null,
    val subRace: DnDEntityMin? = null,
    val background: DnDEntityMin? = null
)