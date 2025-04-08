package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun loadCharactersMinList(): List<CharacterMin>
    suspend fun getCharacterWithModifiers(characterId: Uuid): CharacterWithModifiers
    suspend fun createCharacter(character: Character): Uuid
}