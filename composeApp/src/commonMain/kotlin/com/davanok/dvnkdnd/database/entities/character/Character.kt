package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import okio.Path
import kotlin.uuid.Uuid

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["race"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDRace::class, ["id"], ["sub_race"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBackground::class, ["id"], ["background"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBackground::class, ["id"], ["sub_background"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class Character(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val description: String,
    @ColumnInfo(index = true) val race: Uuid?,
    @ColumnInfo("sub_race", index = true) val subRace: Uuid?,
    @ColumnInfo(index = true) val background: Uuid?,
    @ColumnInfo("sub_background", index = true) val subBackground: Uuid?,
    val level: Int = 1,
    @ColumnInfo("proficiency_bonus") val proficiencyBonus: Int = 2,
    val source: String? = null,
    @ColumnInfo("image") val image: Path? = null
)

@Entity(
    tableName = "character_classes",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDClass::class, ["id"], ["class_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDClass::class, ["id"], ["sub_class_id"], onDelete = ForeignKey.SET_NULL)
    ],
    primaryKeys = ["character_id", "class_id"]
)
data class CharacterClass(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("class_id", index = true) val classId: Uuid,
    @ColumnInfo("sub_class_id", index = true) val subClassId: Uuid?
)