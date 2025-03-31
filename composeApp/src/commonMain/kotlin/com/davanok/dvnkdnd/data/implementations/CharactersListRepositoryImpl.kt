package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.repositories.CharactersListRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import kotlinx.coroutines.flow.Flow

class CharactersListRepositoryImpl(
    private val dao: CharactersDao
) : CharactersListRepository {
    override fun getCharactersFlow(): Flow<List<CharacterMin>> = dao.getCharactersFlow()
}