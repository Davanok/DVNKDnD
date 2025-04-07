package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.DnDProficiency

@Dao
interface ProficienciesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(proficiency: DnDProficiency)
}