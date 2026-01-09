package com.davanok.dvnkdnd.domain.repositories.local

import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull>
    fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>>

    fun getCharactersWithImagesListFlow(): Flow<Result<List<CharacterMin>>>

    suspend fun saveCharacter(character: CharacterFull): Result<Uuid>

    suspend fun setCharacterHealth(characterId: Uuid, health: CharacterHealth): Result<Unit>
    suspend fun setCharacterNote(characterId: Uuid, note: CharacterNote): Result<Unit>
    suspend fun deleteCharacterNote(noteId: Uuid): Result<Unit>
    suspend fun setCharacterUsedSpells(characterId: Uuid, typeId: Uuid?, usedSpells: IntArray): Result<Unit>
}