@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Size
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "races",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDRace(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val raceId: Uuid,
    val size: Size
)