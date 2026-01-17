package com.davanok.dvnkdnd.data.local.implementations

import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterFull
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterMin
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterHealth
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterItemLink
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterNote
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterStateLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterItemLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin
import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.domain.entities.character.CharacterStateLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
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
    override fun getCharactersWithImagesListFlow(): Flow<Result<List<CharacterMin>>> =
        dao.getCharactersWithImageListFlow().mapLatest { characters ->
            Result.success(characters.map { it.toCharacterMin() })
        }.catch { thr -> emit(Result.failure(thr)) }

    override suspend fun saveCharacter(character: CharacterFull) =
        runLogging("saveCharacter") {
            dao.saveCharacter(character)
        }

    override suspend fun setCharacterHealth(characterId: Uuid, health: CharacterHealth) =
        runLogging("setCharacterHealth") {
            dao.insertCharacterHealth(health.toDbCharacterHealth(characterId))
        }

    override suspend fun setCharacterNote(characterId: Uuid, note: CharacterNote) =
        runLogging("setCharacterNote") {
            dao.insertCharacterNotes(listOf(note.toDbCharacterNote(characterId)))
        }

    override suspend fun deleteCharacterNote(noteId: Uuid) =
        runLogging("setCharacterNote") {
            dao.deleteCharacterNote(noteId)
        }

    override suspend fun setCharacterUsedSpells(characterId: Uuid, typeId: Uuid?, usedSpells: IntArray) =
        runLogging("setCharacterUsedSpells") {
            dao.setCharacterUsedSpells(
                DbCharacterUsedSpellSlots(
                    characterId = characterId,
                    spellSlotTypeId = typeId,
                    usedSpells = usedSpells.toList()
                )
            )
        }

    override suspend fun setCharacterItem(characterId: Uuid, item: CharacterItemLink) =
        runLogging("setCharacterItem") {
            dao.setCharacterItemLink(item.toDbCharacterItemLink(characterId))
        }

    override suspend fun setCharacterState(
        characterId: Uuid,
        state: CharacterStateLink
    ): Result<Unit> = runLogging("setCharacterState") {
        dao.setCharacterState(state.toDbCharacterStateLink(characterId))
    }

    override suspend fun activateCharacterItem(
        characterId: Uuid,
        item: CharacterItemLink,
        activation: FullItemActivation
    ): Result<Unit> = runLogging("activateCharacterItem") {
        dao.activateCharacterItem(characterId, item, activation)
    }
}