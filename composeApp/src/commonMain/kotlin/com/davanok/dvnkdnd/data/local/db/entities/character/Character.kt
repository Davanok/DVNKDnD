package com.davanok.dvnkdnd.data.local.db.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import kotlin.uuid.Uuid

@Entity(tableName = "characters")
data class DbCharacter(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("user_id")
    val userId: Uuid? = null,
    val name: String,
    val description: String,
    val level: Int = 1,
    val source: String? = null,
    @ColumnInfo("image")
    val image: String? = null
)

@Entity(
    tableName = "character_main_entities",
    primaryKeys = ["character_id", "entity_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbBaseEntity::class, ["id"], ["sub_entity_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class DbCharacterMainEntity(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    @ColumnInfo("sub_entity_id", index = true) val subEntityId: Uuid?,
    val level: Int
)