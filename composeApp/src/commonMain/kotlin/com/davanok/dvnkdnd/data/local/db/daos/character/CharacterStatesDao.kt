package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink

@Dao
interface CharacterStatesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterStates(states: List<DbCharacterStateLink>)
}