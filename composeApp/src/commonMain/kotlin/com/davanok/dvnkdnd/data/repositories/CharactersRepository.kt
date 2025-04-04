package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun getCharactersFlow(): Flow<List<CharacterMin>>
    suspend fun getCharacterWithModifiers(): CharacterWithModifiers
}