@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class NewCharacterRepositoryImpl(
    private val entitiesDao: EntitiesDao,
    private val charactersDao: CharactersDao
): NewCharacterRepository {
    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) =
        entitiesDao.getEntitiesWithSubList(type).fastMap { it.toEntityWithSubEntities() }

    override suspend fun createCharacter(character: Character): Uuid {
        charactersDao.insertCharacter(character)
        return character.id
    }

    override suspend fun getExistingEntities(entityWebIds: List<Uuid>): List<Uuid> =
        entitiesDao.getExistingEntities(entityWebIds)
}