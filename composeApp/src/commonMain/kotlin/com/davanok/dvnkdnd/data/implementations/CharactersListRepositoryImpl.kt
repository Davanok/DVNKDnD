package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.CharactersListRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.entities.character.Character

class CharactersListRepositoryImpl(
    private val charactersDao: CharactersDao
): CharactersListRepository {
    override suspend fun getCharactersList(): List<Character> {
        TODO("Not yet implemented")
    }
}