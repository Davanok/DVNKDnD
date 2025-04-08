package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import okio.Path
import kotlin.uuid.Uuid

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["race"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["subRace"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["background"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["subBackground"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class Character(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val description: String,
    @ColumnInfo(index = true) val race: Uuid?,
    @ColumnInfo(index = true) val subRace: Uuid?,
    @ColumnInfo(index = true) val background: Uuid?,
    @ColumnInfo(index = true) val subBackground: Uuid?,
    val level: Int = 1,
    @ColumnInfo("proficiency_bonus") val proficiencyBonus: Int = 2,
    val source: String? = null,
    @ColumnInfo("main_image") val mainImage: Path? = null
)
@Entity(
    tableName = "character_classes",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDClass::class, ["id"], ["class_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterClasses(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("class_id", index = true) val classId: Uuid,
)