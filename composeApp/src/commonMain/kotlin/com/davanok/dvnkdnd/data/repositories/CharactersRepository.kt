package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllSkills
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull?>

    suspend fun getCharactersMinList(): Result<List<CharacterMin>>
    suspend fun getCharacterWithAllModifiers(characterId: Uuid): Result<CharacterWithAllModifiers>
    suspend fun getCharacterWithAllSkills(characterId: Uuid): Result<CharacterWithAllSkills>

    suspend fun createCharacter(character: Character, classId: Uuid, subClassId: Uuid?): Result<Uuid>

    suspend fun setCharacterStats(characterId: Uuid, modifiers: DnDModifiersGroup): Result<Unit>
    suspend fun setCharacterSelectedModifierBonuses(characterId: Uuid, bonusIds: List<Uuid>): Result<Unit>
    suspend fun setCharacterSelectedSkills(characterId: Uuid, skillIds: List<Uuid>): Result<Unit>
}