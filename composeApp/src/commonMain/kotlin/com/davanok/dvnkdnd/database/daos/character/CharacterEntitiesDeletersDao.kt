package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Query
import kotlin.uuid.Uuid

@Dao
interface CharacterEntitiesDeletersDao {
    @Query("DELETE FROM character_notes WHERE id = :noteId")
    suspend fun deleteCharacterNote(noteId: Uuid)
}