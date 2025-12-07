package com.davanok.dvnkdnd.data.local.db.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import kotlin.uuid.Uuid

@Entity(
    tableName = "base_entities",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["parent_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbBaseEntity(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("parent_id", index = true) val parentId: Uuid? = null,
    @ColumnInfo("user_id") val userId: Uuid? = null,
    @ColumnInfo(index = true) val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String,
    @ColumnInfo("image")
    val image: String? = null
)
@Entity(
    tableName = "entity_full_descriptions",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityFullDescription(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val text: String
)

@Entity(
    tableName = "entity_images",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class DbEntityImage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("entity_id", index = true) val entityId: Uuid?,
    val path: String
)
