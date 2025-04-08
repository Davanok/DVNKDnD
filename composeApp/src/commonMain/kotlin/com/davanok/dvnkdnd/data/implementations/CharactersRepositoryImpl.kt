package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun loadCharactersMinList() = dao.loadCharactersMinList()

    override suspend fun getCharacterWithModifiers(characterId: Uuid) =
        dao.getCharacterWithModifiers(characterId).toCharacterWithModifiers()


    override suspend fun createCharacter(character: Character): Uuid {
        dao.insertCharacter(character)
        return character.id
    }
}