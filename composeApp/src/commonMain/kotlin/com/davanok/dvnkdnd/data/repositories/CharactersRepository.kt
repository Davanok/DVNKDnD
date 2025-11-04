package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.model.entities.character.CharacterNote
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull>
    fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>>

    fun getCharactersMinListFlow(): Flow<Result<List<CharacterBase>>>

    suspend fun saveCharacter(character: CharacterFull): Result<Uuid>

    suspend fun setCharacterHealth(characterId: Uuid, health: DnDCharacterHealth): Result<Unit>
    suspend fun setCharacterNote(characterId: Uuid, note: CharacterNote): Result<Unit>
    suspend fun deleteCharacterNote(noteId: Uuid): Result<Unit>
}