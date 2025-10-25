package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull?>
    fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull?>>

    suspend fun getCharactersMinList(): Result<List<CharacterBase>>

    suspend fun saveCharacter(character: CharacterFull): Result<Uuid>
}