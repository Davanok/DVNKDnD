package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClass
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifiers
import kotlin.collections.plus
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun loadCharactersMinList() = dao.loadCharactersMinList()

    override suspend fun getCharacterWithModifiers(characterId: Uuid) =
        dao.getCharacterWithModifiers(characterId).toCharacterWithModifiers()


    override suspend fun createCharacter(character: Character, classId: Uuid, subClassId: Uuid?): Uuid {
        dao.insertCharacter(character)
        dao.insertCharacterClass(CharacterClass(character.id, classId, subClassId))

        val characterWithModifiers = dao.getCharacterWithModifiers(character.id)
            .toCharacterWithModifiers()

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

        setCharacterSelectedModifiers(characterId = character.id, modifierIds = notSelectableModifiers)

        return character.id
    }

    override suspend fun setCharacterSelectedModifiers(characterId: Uuid, modifierIds: List<Uuid>) {
        val entities = modifierIds.fastMap { modifierId ->
            CharacterSelectedModifiers(characterId, modifierId)
        }
        dao.insertCharacterSelectedModifiers(entities)
    }
}