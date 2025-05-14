package com.davanok.dvnkdnd.database.entities.dndEntities.concept

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Size
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Entity(
    tableName = "races",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDRace(
    // also subrace
    @PrimaryKey val id: Uuid,
    val speed: Int,
    val size: Size,
)