package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlin.uuid.Uuid

interface CharactersRepository {
    suspend fun loadCharactersMinList(): List<CharacterMin>
    suspend fun getCharacterWithModifiers(characterId: Uuid): CharacterWithModifiers
    suspend fun createCharacter(character: Character, classId: Uuid, subClassId: Uuid?): Uuid

    suspend fun setCharacterStats(characterId: Uuid, modifiers: DnDModifiersGroup)
    suspend fun setCharacterSelectedModifierBonuses(characterId: Uuid, bonusIds: List<Uuid>)
}