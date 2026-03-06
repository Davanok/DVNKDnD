package com.davanok.dvnkdnd.ui.pages.editCharacter

import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes

sealed interface EditCharacterScreenEvent {
    data class ShowAddEntityDialog(val entityType: DnDEntityTypes) : EditCharacterScreenEvent
    data object HideAddEntityDialog : EditCharacterScreenEvent
    data class AddCharacterEntity(val entity: DnDEntityMin, val subEntity: DnDEntityMin?) : EditCharacterScreenEvent

    data class UpdateCharacterBase(val character: CharacterBase) : EditCharacterScreenEvent
    data class SetCharacterMainEntityLevel(val entity: DnDEntityMin, val level: Int) : EditCharacterScreenEvent
    data class RemoveCharacterEntity(val entity: DnDEntityMin) : EditCharacterScreenEvent

    data class UpdateAttributes(val attributes: AttributesGroup) : EditCharacterScreenEvent
    data class SetModifierSelection(val modifier: DnDModifier, val selected: Boolean) : EditCharacterScreenEvent

    data class SetCharacterCustomModifier(val modifier: CharacterCustomModifier) : EditCharacterScreenEvent
    data class DeleteCharacterCustomModifier(val modifier: CharacterCustomModifier) : EditCharacterScreenEvent

    data class UpdateOptionalValues(val values: CharacterOptionalValues) : EditCharacterScreenEvent
}