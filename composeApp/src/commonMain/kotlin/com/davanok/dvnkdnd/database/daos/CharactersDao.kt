package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import kotlinx.coroutines.flow.Flow

@Dao
interface CharactersDao {
    @Query("SELECT id, name, level FROM characters")
    fun getCharactersFlow(): Flow<List<CharacterMin>>
}