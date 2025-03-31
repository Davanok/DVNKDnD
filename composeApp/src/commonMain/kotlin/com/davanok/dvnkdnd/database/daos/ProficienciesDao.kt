package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import com.davanok.dvnkdnd.database.entities.Proficiency

@Dao
interface ProficienciesDao {
    @Insert
    suspend fun insert(proficiency: Proficiency)
}