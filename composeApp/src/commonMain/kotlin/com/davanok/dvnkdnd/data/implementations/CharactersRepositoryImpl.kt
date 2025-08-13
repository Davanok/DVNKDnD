package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull?> = runCatching {
        dao.getFullCharacter(characterId)?.toCharacterFull()
    }

    override suspend fun getCharactersMinList() = runCatching {
        dao.getCharactersMinList()
    }
}