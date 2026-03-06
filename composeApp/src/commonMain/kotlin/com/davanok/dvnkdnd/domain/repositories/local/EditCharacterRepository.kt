package com.davanok.dvnkdnd.domain.repositories.local

import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface EditCharacterRepository {
    fun getFullCharacterFlow(characterId: Uuid): Flow<Result<CharacterFull>>

    suspend fun setCharacterBase(characterBase: CharacterBase): Result<Unit>
    suspend fun setModifierSelection(
        characterId: Uuid,
        modifier: DnDModifier,
        selected: Boolean
    ): Result<Unit>

    suspend fun setCustomModifier(
        characterId: Uuid,
        modifier: CharacterCustomModifier
    ): Result<Unit>

    suspend fun deleteCustomModifier(
        characterId: Uuid,
        modifier: CharacterCustomModifier
    ): Result<Unit>

    suspend fun setCharacterAttributes(characterId: Uuid, attributes: AttributesGroup): Result<Unit>

    suspend fun setCharacterOptionalValues(
        characterId: Uuid,
        values: CharacterOptionalValues
    ): Result<Unit>

    suspend fun setCharacterMainEntity(
        characterId: Uuid,
        entityLink: CharacterMainEntityLink
    ): Result<Unit>
    suspend fun setCharacterMainEntityLevel(
        characterId: Uuid,
        entity: DnDEntityMin,
        level: Int
    ): Result<Unit>

    suspend fun setCharacterFeat(characterId: Uuid, feat: DnDEntityMin): Result<Unit>

    suspend fun setCharacterSpell(
        characterId: Uuid,
        spell: DnDEntityMin,
        ready: Boolean
    ): Result<Unit>

    suspend fun setCharacterItem(
        characterId: Uuid,
        item: DnDEntityMin,
        equipped: Boolean,
        attuned: Boolean,
        count: Int?
    ): Result<Unit>

    suspend fun setCharacterState(
        characterId: Uuid,
        state: DnDEntityMin,
        source: DnDEntityMin?,
        deletable: Boolean
    ): Result<Unit>
}