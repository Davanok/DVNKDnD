package com.davanok.dvnkdnd.domain.repositories.local

import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface EditCharacterRepository {
    fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>>
}