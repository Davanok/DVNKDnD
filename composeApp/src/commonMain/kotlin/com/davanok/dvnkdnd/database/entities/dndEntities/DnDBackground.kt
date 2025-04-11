package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    "backgrounds",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDBackground( // also sub background
    @PrimaryKey val id: Uuid
)
