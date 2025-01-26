package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.davanok.dvnkdnd.data.model.DnDEntityMin
import kotlinx.coroutines.flow.Flow

@Dao
interface NewCharacterDao {
    @Query("SELECT id, name, source FROM classes WHERE source == :source")
    suspend fun getClassesMinList(source: String): List<DnDEntityMin>
}