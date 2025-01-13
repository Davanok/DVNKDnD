package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.types.dnd_enums.Skills
import com.davanok.dvnkdnd.database.entities.Proficiency


@Entity(tableName = "backgrounds")
data class DnDBackground(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val source: String?
)

@Entity(
    tableName = "background_skills",
    foreignKeys = [
        ForeignKey(DnDBackground::class, ["id"], ["backgroundId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class BackgroundSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val backgroundId: Long,
    val skill: Skills
)
@Entity(
    tableName = "background_proficiencies",
    foreignKeys = [
        ForeignKey(DnDBackground::class, ["id"], ["backgroundId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class BackgroundProficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val backgroundId: Long,
    @ColumnInfo(index = true) val proficiencyId: Long
)
