package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifierBonus
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedSkill
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull?> = runCatching {
        dao.getFullCharacter(characterId)?.toCharacterFull()
    }

    override suspend fun getCharactersMinList() = runCatching {
        dao.getCharactersMinList()
    }

    override suspend fun getCharacterWithAllModifiers(characterId: Uuid) = runCatching {
        dao.getCharacterWithAllModifiers(characterId).toCharacterWithAllModifiers()
    }

    override suspend fun getCharacterWithAllSkills(characterId: Uuid) = runCatching {
        dao.getCharacterWithAllSkills(characterId).toCharacterWithAllSkills()
    }

    override suspend fun setCharacterStats(
        characterId: Uuid,
        modifiers: DnDModifiersGroup,
    ) = runCatching {
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
    ) = runCatching {
        val entities = bonusIds.fastMap { modifierId ->
            CharacterSelectedModifierBonus(characterId, modifierId)
        }
        dao.insertCharacterSelectedModifierBonuses(entities)
    }

    override suspend fun setCharacterSelectedSkills(
        characterId: Uuid,
        skillIds: List<Uuid>
    ) = runCatching {
        val entities = skillIds.fastMap { skillId ->
            CharacterSelectedSkill(characterId, skillId)
        }
        dao.insertCharacterSelectedSkills(entities)
    }
}