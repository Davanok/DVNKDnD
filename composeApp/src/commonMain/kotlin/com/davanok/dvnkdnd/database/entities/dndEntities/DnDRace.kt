package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.types.dnd_enums.Size
import com.davanok.dvnkdnd.data.types.dnd_enums.Skills
import com.davanok.dvnkdnd.data.types.dnd_enums.Stats

@Entity(tableName = "races")
data class DnDRace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val speed: Int,
    val source: String,
    val modifierSelectLimit: Int?,
    val skillsSelectLimit: Int?,
)

@Entity(
    tableName = "race_sizes",
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["raceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class RaceSize(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val raceId: Long,
    val size: Size
)
@Entity(
    tableName = "race_modifiers",
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["raceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class RaceModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val raceId: Long,
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int
)
@Entity(
    tableName = "race_skills",
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["raceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class RaceSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val raceId: Long,
    val selectable: Boolean,
    val skill: Skills
)