package com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import kotlin.uuid.Uuid

@Entity(
    "backgrounds",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbBackground( // also sub background
    @PrimaryKey val id: Uuid
)
