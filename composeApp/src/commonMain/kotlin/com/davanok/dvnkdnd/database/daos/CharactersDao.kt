package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.room.RoomRawQuery

@Dao
interface CharactersDao {

    @RawQuery
    suspend fun getRawQuery(query: RoomRawQuery): List<String>
}