package com.davanok.dvnkdnd.data.local.implementations

import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.core.utils.toResultFlow
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterFull
import com.davanok.dvnkdnd.data.local.mappers.character.toCharacterMin
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterHealth
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterItemLink
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterNote
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterSpell
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterStateLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterItemLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin
import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpellLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterStateLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class CharactersRepositoryImpl(
    private val dao: CharactersDao,
    logger: Logger
) : CharactersRepository {
    val logger = logger.withTag(TAG)
    
    override suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull> =
        logger.runLogging("getFullCharacter") {
            dao.getFullCharacter(characterId).toCharacterFull()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>> =
        dao.getFullCharacterFlow(characterId)
            .mapNotNull { it.toCharacterFull() }
            .toResultFlow("getFullCharacterFlow", logger)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCharactersWithImagesListFlow(): Flow<Result<List<CharacterMin>>> =
        dao.getCharactersWithImageListFlow()
            .map { it.map { c -> c.toCharacterMin() } }
            .toResultFlow("getCharactersWithImagesListFlow", logger)

    override suspend fun saveCharacter(character: CharacterFull) =
        logger.runLogging("saveCharacter") {
            dao.saveCharacter(character)
        }

    override suspend fun setCharacterHealth(characterId: Uuid, health: CharacterHealth) =
        logger.runLogging("setCharacterHealth") {
            dao.insertCharacterHealth(health.toDbCharacterHealth(characterId))
        }

    override suspend fun setCharacterNote(characterId: Uuid, note: CharacterNote) =
        logger.runLogging("setCharacterNote") {
            dao.insertCharacterNotes(listOf(note.toDbCharacterNote(characterId)))
        }

    override suspend fun deleteCharacterNote(noteId: Uuid) =
        logger.runLogging("setCharacterNote") {
            dao.deleteCharacterNote(noteId)
        }

    override suspend fun setCharacterUsedSpells(characterId: Uuid, typeId: Uuid?, usedSpells: IntArray) =
        logger.runLogging("setCharacterUsedSpells") {
            dao.setCharacterUsedSpells(
                DbCharacterUsedSpellSlots(
                    characterId = characterId,
                    spellSlotTypeId = typeId,
                    usedSpells = usedSpells.toList()
                )
            )
        }

    override suspend fun setCharacterItem(characterId: Uuid, item: CharacterItemLink) =
        logger.runLogging("setCharacterItem") {
            dao.setCharacterItemLink(item.toDbCharacterItemLink(characterId))
        }

    override suspend fun setCharacterState(
        characterId: Uuid,
        state: CharacterStateLink
    ): Result<Unit> = logger.runLogging("setCharacterState") {
        dao.setCharacterState(state.toDbCharacterStateLink(characterId))
    }

    override suspend fun deleteCharacterState(
        characterId: Uuid,
        state: CharacterStateLink
    ): Result<Unit> = logger.runLogging("deleteCharacterState") {
        dao.deleteCharacterState(state.toDbCharacterStateLink(characterId))
    }

    override suspend fun setCharacterSpell(
        characterId: Uuid,
        spell: CharacterSpellLink
    ): Result<Unit> = logger.runLogging("setCharacterSpell") {
        dao.setCharacterSpell(spell.toDbCharacterSpell(characterId))
    }

    override suspend fun activateCharacterItem(
        characterId: Uuid,
        item: CharacterItemLink,
        activation: FullItemActivation
    ): Result<Unit> = logger.runLogging("activateCharacterItem") {
        dao.activateCharacterItem(characterId, item, activation)
    }
    
    companion object {
        private const val TAG = "CharacterRepository"
    }
}