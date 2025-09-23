package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import okio.Path
import kotlin.uuid.Uuid

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("user_id")
    val userId: Uuid? = null,
    val name: String,
    val description: String,
    val level: Int = 1,
    @ColumnInfo("proficiency_bonus")
    val proficiencyBonus: Int? = null, // null if calculate
    val source: String? = null,
    @ColumnInfo("image")
    val image: Path? = null
)

@Entity(
    tableName = "character_main_entities",
    primaryKeys = ["character_id", "entity_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDBaseEntity::class, ["id"], ["sub_entity_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class CharacterMainEntity(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    @ColumnInfo("sub_entity_id", index = true) val subEntityId: Uuid?,
    val level: Int
)