package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterNote
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.util.runLogging
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.character.CharactersDao
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterBase
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterHealth
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterNote
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

    override suspend fun setCharacterHealth(characterId: Uuid, health: DnDCharacterHealth) =
        runLogging("setCharacterHealth") {
            dao.insertCharacterHealth(health.toCharacterHealth(characterId))
        }

    override suspend fun setCharacterNote(characterId: Uuid, note: CharacterNote) =
        runLogging("setCharacterNote") {
            dao.insertCharacterNotes(listOf(note.toDbCharacterNote(characterId)))
        }

    override suspend fun deleteCharacterNote(noteId: Uuid) =
        runLogging("setCharacterNote") {
            dao.deleteCharacterNote(noteId)
        }
}