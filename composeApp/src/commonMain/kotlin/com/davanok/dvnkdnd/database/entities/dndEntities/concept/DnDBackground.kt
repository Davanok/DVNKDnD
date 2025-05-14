package com.davanok.dvnkdnd.database.entities.dndEntities.concept

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Entity(
    "backgrounds",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDBackground( // also sub background
    @PrimaryKey val id: Uuid
)
