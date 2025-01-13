package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.types.dnd_enums.Skills
import com.davanok.dvnkdnd.data.types.dnd_enums.Stats

@Entity(tableName = "sub_races")
data class DnDSubRace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val speed: Int,
    val source: String,
    val modifierSelectLimit: Int?,
    val skillsSelectLimit: Int?,
)

@Entity(
    tableName = "sub_race_modifiers",
    foreignKeys = [
        ForeignKey(DnDSubRace::class, ["id"], ["subRaceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SubRaceModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val subRaceId: Long,
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int
)
@Entity(
    tableName = "sub_race_skills",
    foreignKeys = [
        ForeignKey(DnDSubRace::class, ["id"], ["subRaceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SubRaceSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val subRaceId: Long,
    val selectable: Boolean,
    val skill: Skills
)