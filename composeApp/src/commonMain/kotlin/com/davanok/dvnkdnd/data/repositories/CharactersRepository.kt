package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers

interface CharactersRepository {
    suspend fun loadCharactersMinList(): List<CharacterMin>
    suspend fun getCharacterWithModifiers(): CharacterWithModifiers
}