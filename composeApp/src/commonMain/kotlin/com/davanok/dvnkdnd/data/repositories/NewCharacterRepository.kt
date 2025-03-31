package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin

interface NewCharacterRepository {
    suspend fun getClassesMinList(source: String): List<DnDEntityMin>
    suspend fun getRacesMinList(source: String): List<DnDEntityMin>
    suspend fun getBackgroundsMinList(source: String): List<DnDEntityMin>

    suspend fun getSubClassesMinList(clsId: Long, source: String): List<DnDEntityMin>
    suspend fun getSubRacesMinList(raceId: Long, source: String): List<DnDEntityMin>
}