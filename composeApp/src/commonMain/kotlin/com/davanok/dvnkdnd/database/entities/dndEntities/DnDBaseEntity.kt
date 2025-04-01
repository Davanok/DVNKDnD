package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes

@Entity(tableName = "base_entities")
data class DnDBaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String?,
    val fullDescription: String?,
)

@Entity(
    tableName = "entity_images",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class EntityImages(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long?,
    val path: String
)