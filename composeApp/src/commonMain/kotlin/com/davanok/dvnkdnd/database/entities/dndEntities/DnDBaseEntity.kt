@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "base_entities",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["parentId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDBaseEntity(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val parentId: Uuid? = null,
    @ColumnInfo(index = true) val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String
)
@Entity(
    tableName = "entity_full_descriptions",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityFullDescription(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val text: String
)

@Entity(
    tableName = "entity_images",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class EntityImage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid?,
    val path: String
)
