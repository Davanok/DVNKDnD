package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.DnDEntityMin
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import com.davanok.dvnkdnd.database.daos.NewCharacterDao

class NewCharacterRepositoryImpl(
    private val dao: NewCharacterDao
): NewCharacterRepository {
    override suspend fun getClassesMinList(source: String): List<DnDEntityMin> = listOf(
        DnDEntityMin(1, "barbarian", "PHB"),
        DnDEntityMin(2, "rogue", "PHB")
    ) // dao.getClassesMinList(source)
}