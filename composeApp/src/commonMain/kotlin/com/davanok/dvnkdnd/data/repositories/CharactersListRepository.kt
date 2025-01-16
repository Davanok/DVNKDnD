package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.database.entities.character.CharacterMin
import kotlinx.coroutines.flow.Flow

interface CharactersListRepository {
    fun getCharactersFlow(): Flow<List<CharacterMin>>
}