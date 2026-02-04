package com.davanok.dvnkdnd.ui.pages.editCharacter

import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup

sealed interface EditCharacterScreenEvent {
    data class UpdateAttributes(val attributes: AttributesGroup) : EditCharacterScreenEvent
}