package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun loadCharactersMinList() = dao.loadCharactersMinList()

    override suspend fun getCharacterWithModifiers() =
        dao.getCharacterWithModifiers().toCharacterWithModifiers()
}