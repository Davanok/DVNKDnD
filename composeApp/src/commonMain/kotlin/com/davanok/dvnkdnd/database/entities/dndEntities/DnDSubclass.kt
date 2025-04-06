@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "subclasses",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DnDSubclass(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
)