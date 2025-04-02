package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subclasses",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DnDSubclass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
)