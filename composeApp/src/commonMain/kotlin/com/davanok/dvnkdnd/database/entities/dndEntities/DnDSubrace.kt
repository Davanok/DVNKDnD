package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats

@Entity(
    tableName = "subraces",
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["raceId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DnDSubrace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val raceId: Long,
    val name: String,
    val description: String,
    val speed: Int,
    val source: String,
    val modifierSelectLimit: Int?,
    val skillsSelectLimit: Int?,
)

@Entity(
    tableName = "subrace_modifiers",
    foreignKeys = [
        ForeignKey(DnDSubrace::class, ["id"], ["subraceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SubraceModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val subraceId: Long,
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int
)
@Entity(
    tableName = "subrace_skills",
    foreignKeys = [
        ForeignKey(DnDSubrace::class, ["id"], ["subraceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SubraceSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val subraceId: Long,
    val selectable: Boolean,
    val skill: Skills
)