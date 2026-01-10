package com.davanok.dvnkdnd.ui.pages.characterFull

import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import kotlin.uuid.Uuid

sealed interface CharacterFullScreenContract {
    data class SetHealth(val health: CharacterHealth) : CharacterFullScreenContract

    data class UpdateOrNewNote(val note: CharacterNote) : CharacterFullScreenContract
    data class DeleteNote(val note: CharacterNote) : CharacterFullScreenContract

    data class SetUsedSpellsCount(val typeId: Uuid?, val level: Int, val count: Int) : CharacterFullScreenContract

    data class UpdateCharacterItem(val item: CharacterItem) : CharacterFullScreenContract
    data class ActivateCharacterItem(val item: CharacterItem, val activation: FullItemActivation) : CharacterFullScreenContract
}