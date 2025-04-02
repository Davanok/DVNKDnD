package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.EntitiesDao

class NewCharacterRepositoryImpl(
    private val entitiesDao: EntitiesDao,
    private val charactersDao: CharactersDao
): NewCharacterRepository {
    override suspend fun getEntitiesMinList(
        type: DnDEntityTypes,
        parentId: Long?,
    ) = entitiesDao.getEntitiesMinList(type, parentId)
}