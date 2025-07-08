package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.model.DbCharacterWithAllSkills
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun getCharactersMinList(): List<CharacterMin>
    suspend fun getCharacterWithAllModifiers(characterId: Uuid): CharacterWithAllModifiers
    suspend fun getCharacterWithAllSkills(characterId: Uuid): DbCharacterWithAllSkills

    suspend fun createCharacter(character: Character, classId: Uuid, subClassId: Uuid?): Uuid

    suspend fun setCharacterStats(characterId: Uuid, modifiers: DnDModifiersGroup)
    suspend fun setCharacterSelectedModifierBonuses(characterId: Uuid, bonusIds: List<Uuid>)
}