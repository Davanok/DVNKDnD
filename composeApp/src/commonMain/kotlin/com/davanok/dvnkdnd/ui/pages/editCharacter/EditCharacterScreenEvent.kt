package com.davanok.dvnkdnd.ui.pages.editCharacter

import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import kotlin.uuid.Uuid

sealed interface EditCharacterScreenEvent {
    data class UpdateCharacterBase(val character: CharacterBase) : EditCharacterScreenEvent
    data class SetCharacterEntityLevel(val entityId: Uuid, val level: Int) : EditCharacterScreenEvent
    data class AddCharacterEntity(val entityLink: CharacterMainEntityLink) : EditCharacterScreenEvent
    data class RemoveCharacterEntity(val entityLink: CharacterMainEntityLink) : EditCharacterScreenEvent

    data class UpdateAttributes(val attributes: AttributesGroup) : EditCharacterScreenEvent
}