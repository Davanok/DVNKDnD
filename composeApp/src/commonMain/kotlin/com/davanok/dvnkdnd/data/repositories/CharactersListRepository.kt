package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.database.entities.character.Character

interface CharactersListRepository {
    suspend fun getCharactersList(): List<Character>
}