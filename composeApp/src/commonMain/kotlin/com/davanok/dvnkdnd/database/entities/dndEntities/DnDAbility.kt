package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "abilities",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDAbility(
    @PrimaryKey val id: Uuid,
)
