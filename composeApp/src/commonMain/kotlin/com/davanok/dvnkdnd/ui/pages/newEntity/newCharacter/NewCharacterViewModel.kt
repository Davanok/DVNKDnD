package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.types.util.WhileUiSubscribed
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource


enum class SideScreenContent {
    NONE, CLASS, RACE, BACKGROUND
}

data class NewCharacterUiState(
    val isLoading: Boolean = false,
    val message: StringResource? = null,
    val sideScreenContent: SideScreenContent = SideScreenContent.NONE
)
data class CreateCharacterState(
    val image: PlatformFile? = null,
    val name: String = "",
    val description: String = ""
)

class NewCharacterViewModel(

) : ViewModel() {
    private val _message = MutableStateFlow<StringResource?>(null)
    private val _sideScreenContent = MutableStateFlow<SideScreenContent>(SideScreenContent.NONE)

    val uiState: StateFlow<NewCharacterUiState> = combine(
        _message, _sideScreenContent
    ) { message, sideScreenContent ->
        NewCharacterUiState(
            isLoading = false,
            message = message,
            sideScreenContent = sideScreenContent
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NewCharacterUiState(isLoading = true)
    )

    private val _characterImage = MutableStateFlow<PlatformFile?>(null)
    private val _characterName = MutableStateFlow("")
    private val _characterDescription = MutableStateFlow("")

    val createCharacterState: StateFlow<CreateCharacterState> = combine(
        _characterImage, _characterName, _characterDescription
    ) { image, name, description ->
        CreateCharacterState(
            image,
            name,
            description
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CreateCharacterState()
    )
    fun setCharacterName(value: String) { _characterName.value = value }
    fun setCharacterDescription(value: String) { _characterDescription.value = value }
}