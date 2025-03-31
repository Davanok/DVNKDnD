package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import kotlinx.coroutines.flow.Flow

interface CharactersListRepository {
    fun getCharactersFlow(): Flow<List<CharacterMin>>
}