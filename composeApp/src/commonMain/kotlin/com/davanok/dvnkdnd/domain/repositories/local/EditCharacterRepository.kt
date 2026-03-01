package com.davanok.dvnkdnd.domain.repositories.local

import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface EditCharacterRepository {
    fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>>

    suspend fun setModifierSelection(characterId: Uuid, modifier: DnDModifier, selected: Boolean): Result<Unit>
    suspend fun setCustomModifier(characterId: Uuid, modifier: CharacterCustomModifier): Result<Unit>
    suspend fun deleteCustomModifier(characterId: Uuid, modifier: CharacterCustomModifier): Result<Unit>
    suspend fun setCharacterAttributes(characterId: Uuid, attributes: AttributesGroup): Result<Unit>

    suspend fun setCharacterOptionalValues(characterId: Uuid, values: CharacterOptionalValues): Result<Unit>
}