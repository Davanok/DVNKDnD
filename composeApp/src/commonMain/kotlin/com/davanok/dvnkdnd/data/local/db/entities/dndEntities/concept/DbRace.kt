package com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.domain.enums.dndEnums.Size
import kotlin.uuid.Uuid

@Entity(
    tableName = "races",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbRace(
    // also subrace
    @PrimaryKey val id: Uuid,
    val speed: Int,
    val size: Size,
)