package com.davanok.dvnkdnd.data.local.db.entities.dndEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import kotlin.uuid.Uuid

@Entity(
    "race_speed_values",
    foreignKeys = [
        ForeignKey(DbRace::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbRaceSpeedValues(
    @PrimaryKey val id: Uuid,
    val walk: Int,
    val swim: Int,
    val fly: Int,
    val climb: Int
)
