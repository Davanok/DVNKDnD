package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink

@Dao
interface CharacterItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCoins(coins: DbCharacterCoins)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterItemLinks(items: List<DbCharacterItemLink>)
}