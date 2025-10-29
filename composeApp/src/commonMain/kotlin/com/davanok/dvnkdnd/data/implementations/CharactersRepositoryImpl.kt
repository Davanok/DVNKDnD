package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.util.runLogging
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.character.CharactersDao
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull> =
        runLogging("getFullCharacter") {
            dao.getFullCharacter(characterId).toCharacterFull()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>> =
        dao.getFullCharacterFlow(characterId).mapLatest {
            Result.success(it.toCharacterFull())
        }.catch { thr -> emit(Result.failure(thr)) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCharactersMinListFlow(): Flow<Result<List<CharacterBase>>> =
        dao.getCharactersMinListFlow().mapLatest { characters ->
            Result.success(characters.map { it.toCharacterBase() })
        }.catch { thr -> emit(Result.failure(thr)) }

    override suspend fun saveCharacter(character: CharacterFull) =
        runLogging("saveCharacter") {
            dao.saveCharacter(character)
        }
}