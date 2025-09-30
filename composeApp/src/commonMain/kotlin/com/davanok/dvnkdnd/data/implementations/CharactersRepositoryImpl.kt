package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.util.runLogging
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.database.daos.character.CharactersDao
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.model.adapters.character.toCharacterBase
import kotlin.uuid.Uuid

class CharactersRepositoryImpl(
    private val dao: CharactersDao,
) : CharactersRepository {
    override suspend fun getFullCharacter(characterId: Uuid): Result<CharacterFull?> =
        runLogging("getFullCharacter") {
            dao.getFullCharacter(characterId)?.toCharacterFull()
        }

    override suspend fun getCharactersMinList() =
        runLogging("getCharactersMinList") {
            dao.getCharactersMinList().fastMap(Character::toCharacterBase)
        }

    override suspend fun saveCharacter(character: CharacterFull) =
        runLogging("saveCharacter") {
            dao.saveCharacter(character)
        }
}