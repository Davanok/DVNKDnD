package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.davanok.dvnkdnd.data.model.DnDEntityMin

@Dao
interface NewCharacterDao {
    @Query("SELECT id, name, source FROM classes WHERE source == :source")
    suspend fun getClassesMinList(source: String): List<DnDEntityMin>
    @Query("SELECT id, name, source FROM races WHERE source == :source")
    suspend fun getRacesMinList(source: String): List<DnDEntityMin>
    @Query("SELECT id, name, source FROM backgrounds WHERE source == :source")
    suspend fun getBackgroundsMinList(source: String): List<DnDEntityMin>

    @Query("SELECT id, name, source FROM subclasses WHERE classId == :clsId AND source = :source")
    suspend fun getSubClassesMinList(clsId: Long, source: String): List<DnDEntityMin>
    @Query("SELECT id, name, source FROM subraces WHERE raceId == :raceId AND source = :source")
    suspend fun getSubRacesMinList(raceId: Long, source: String): List<DnDEntityMin>
}