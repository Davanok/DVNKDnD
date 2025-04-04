package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import kotlinx.coroutines.flow.Flow

class CharactersRepositoryImpl(
    private val dao: CharactersDao
) : CharactersRepository {
    override fun getCharactersFlow(): Flow<List<CharacterMin>> = dao.getCharactersFlow()
    override suspend fun getCharacterWithModifiers(): CharacterWithModifiers =
        dao.getCharacterWithModifiers().toCharacterWithModifiers()
}