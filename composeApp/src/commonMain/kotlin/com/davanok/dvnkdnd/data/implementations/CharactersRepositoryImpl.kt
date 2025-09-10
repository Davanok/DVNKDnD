package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import io.github.aakira.napier.Napier
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull?> =
        runCatching {
            Napier.d { "getFullCharacter: characterId: $characterId" }
            dao.getFullCharacter(characterId)?.toCharacterFull()
        }.onFailure {
            Napier.e("Error in getFullCharacter", it)
        }

    override suspend fun getCharactersMinList() =
        runCatching {
            Napier.d { "getCharactersMinList" }
            dao.getCharactersMinList()
        }.onFailure {
            Napier.e("Error in getCharactersMinList", it)
        }

    override suspend fun saveCharacter(character: CharacterFull) =
        runCatching {
            Napier.d { "saveCharacter" }
            dao.saveCharacter(character)
        }.onFailure {
            Napier.e("Error in saveCharacter", it)
        }
}