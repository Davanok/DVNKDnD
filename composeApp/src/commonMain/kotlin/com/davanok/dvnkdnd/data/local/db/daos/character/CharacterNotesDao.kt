package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import kotlin.uuid.Uuid

interface CharacterNotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterNotes(notes: List<DbCharacterNote>)

    @Query("DELETE FROM character_notes WHERE id = :noteId")
    suspend fun deleteCharacterNote(noteId: Uuid)
}