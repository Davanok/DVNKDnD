package com.davanok.dvnkdnd.data.local.db.entities.character

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "character_settings",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterSettings(
    @PrimaryKey val id: Uuid,
    val valueModifiersCompactView: Boolean,
    val autoLevelChange: Boolean
)
