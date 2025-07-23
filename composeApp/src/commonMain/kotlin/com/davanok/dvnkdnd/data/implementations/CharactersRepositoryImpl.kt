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
    override suspend fun getCharactersMinList() = runCatching {
        dao.getCharactersMinList()
    }

    override suspend fun getCharacterWithAllModifiers(characterId: Uuid) = runCatching {
        dao.getCharacterWithAllModifiers(characterId).toCharacterWithAllModifiers()
    }

    override suspend fun getCharacterWithAllSkills(characterId: Uuid) = runCatching {
        dao.getCharacterWithAllSkills(characterId).toCharacterWithAllSkills()
    }

    private suspend fun implementCharacterNonSelectableBonuses(characterId: Uuid) { // TODO implement skills
        val characterWithModifiers = dao.getCharacterWithAllModifiers(characterId)
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

        setCharacterSelectedModifierBonuses(
            characterId = characterId,
            bonusIds = notSelectableModifiers
        )
    }

    override suspend fun createCharacter(
        character: Character,
        classId: Uuid,
        subClassId: Uuid?
    ): Result<Uuid> = runCatching {
        val characterId = character.id

        dao.insertCharacter(character)
        dao.insertCharacterClass(CharacterClass(characterId, classId, subClassId))

        implementCharacterNonSelectableBonuses(characterId)

        characterId
    }

    override suspend fun setCharacterStats(
        characterId: Uuid,
        modifiers: DnDModifiersGroup,
    ): Result<Unit> = runCatching {
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

    override suspend fun setCharacterSelectedModifierBonuses(
        characterId: Uuid,
        bonusIds: List<Uuid>
    ): Result<Unit> = runCatching {
        val entities = bonusIds.fastMap { modifierId ->
            CharacterSelectedModifierBonus(characterId, modifierId)
        }
        dao.insertCharacterSelectedModifierBonuses(entities)
    }
}