package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import com.davanok.dvnkdnd.database.daos.NewCharacterDao

class NewCharacterRepositoryImpl(
    private val dao: NewCharacterDao
): NewCharacterRepository {
    override suspend fun getClassesMinList(source: String): List<DnDEntityMin> =
        dao.getClassesMinList(source)
    override suspend fun getRacesMinList(source: String): List<DnDEntityMin> =
        dao.getRacesMinList(source)
    override suspend fun getBackgroundsMinList(source: String): List<DnDEntityMin> =
        dao.getBackgroundsMinList(source)

    override suspend fun getSubClassesMinList(clsId: Long, source: String): List<DnDEntityMin> =
        dao.getSubClassesMinList(clsId, source)

    override suspend fun getSubRacesMinList(raceId: Long, source: String): List<DnDEntityMin> =
        dao.getSubRacesMinList(raceId, source)
}