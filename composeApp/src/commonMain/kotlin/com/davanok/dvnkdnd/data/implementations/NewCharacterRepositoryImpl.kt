package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.DnDEntityMin
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import com.davanok.dvnkdnd.database.daos.NewCharacterDao

class NewCharacterRepositoryImpl(
    private val dao: NewCharacterDao
): NewCharacterRepository {
    override suspend fun getClassesMinList(source: String): List<DnDEntityMin> = dao.getClassesMinList(source)
}