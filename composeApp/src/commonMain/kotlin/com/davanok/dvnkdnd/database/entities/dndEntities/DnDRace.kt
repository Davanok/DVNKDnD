package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Size
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Entity(
    tableName = "races",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDRace( // also subrace
    @PrimaryKey val id: Uuid,
    val speed: Int
)
@Serializable
@Entity(
    tableName = "race_sizes",
    primaryKeys = ["raceId", "size"],
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["raceId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class RaceSize(
    @ColumnInfo(index = true) val raceId: Uuid,
    val size: Size
)