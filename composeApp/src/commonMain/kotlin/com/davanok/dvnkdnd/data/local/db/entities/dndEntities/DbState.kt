package com.davanok.dvnkdnd.data.local.db.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.domain.enums.dndEnums.StateType
import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import kotlin.uuid.Uuid

@Entity(
    "states",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbState(
    @PrimaryKey val id: Uuid,
    val type: StateType
)

@Entity(
    "state_duration",
    foreignKeys = [
        ForeignKey(DbState::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbStateDuration(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("time_unit")
    val timeUnit: TimeUnit,
    @ColumnInfo("time_units_count")
    val timeUnitsCount: Int,
)
