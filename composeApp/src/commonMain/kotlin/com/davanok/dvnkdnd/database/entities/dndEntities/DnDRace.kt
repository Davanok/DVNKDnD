package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Size

@Entity(
    tableName = "races",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDRace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
    val speed: Int,
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