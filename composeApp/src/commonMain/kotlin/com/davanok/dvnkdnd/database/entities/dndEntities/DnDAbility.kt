@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "abilities",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDAbility(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
)
