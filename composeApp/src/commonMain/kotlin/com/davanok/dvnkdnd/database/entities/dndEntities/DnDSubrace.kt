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
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDRace::class, ["id"], ["raceId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DnDSubrace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
    @ColumnInfo(index = true) val raceId: Long,
    val speed: Int,
    val modifierSelectLimit: Int?,
    val skillsSelectLimit: Int?,
)