package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage

data class DbCharacterWithImages(
    @Embedded val character: DbCharacter,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_id"
    )
    val images: List<DbCharacterImage>
)
