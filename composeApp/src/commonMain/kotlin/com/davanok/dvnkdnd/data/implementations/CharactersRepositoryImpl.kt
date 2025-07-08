package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClass
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifierBonus
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import kotlin.collections.plus
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun getCharactersMinList() = dao.getCharactersMinList()

    override suspend fun getCharacterWithAllModifiers(characterId: Uuid) =
        dao.getCharacterWithAllModifiers(characterId).toCharacterWithAllModifiers()

    override suspend fun getCharacterWithAllSkills(characterId: Uuid) =
        dao.getCharacterWithAllSkills(characterId)

    override suspend fun createCharacter(
        character: Character,
        classId: Uuid,
        subClassId: Uuid?
    ): Uuid {
        dao.insertCharacter(character)
        dao.insertCharacterClass(CharacterClass(character.id, classId, subClassId))

        val characterWithModifiers = dao.getCharacterWithAllModifiers(character.id)
            .toCharacterWithAllModifiers()

        val notSelectableModifiers =
            (characterWithModifiers.classes + listOfNotNull(
                characterWithModifiers.race,
                characterWithModifiers.subRace,
                characterWithModifiers.background,
                characterWithModifiers.subBackground
            ))
                .fastFlatMap { it.modifiers }
                .fastFilter { !it.selectable }
                .fastMap { it.id }

        setCharacterSelectedModifierBonuses(characterId = character.id, bonusIds = notSelectableModifiers)

        return character.id
    }

    override suspend fun setCharacterStats(
        characterId: Uuid,
        modifiers: DnDModifiersGroup,
    ) {
        val stats = modifiers.run {
            CharacterStats(
                id = characterId,
                strength = strength,
                dexterity = dexterity,
                constitution = constitution,
                intelligence = intelligence,
                wisdom = wisdom,
                charisma = charisma
            )
        }
        dao.insertCharacterStats(stats)
    }

    override suspend fun setCharacterSelectedModifierBonuses(characterId: Uuid, bonusIds: List<Uuid>) {
        val entities = bonusIds.fastMap { modifierId ->
            CharacterSelectedModifierBonus(characterId, modifierId)
        }
        dao.insertCharacterSelectedModifierBonuses(entities)
    }
}