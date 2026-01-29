package com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import kotlin.uuid.Uuid

@Entity(
    tableName = "features",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbState::class, ["id"], ["gives_state_self"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DbState::class, ["id"], ["gives_state_target"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class DbFeature(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("usage_limit_by_level")
    val usageLimitByLevel: List<Int>,
    @ColumnInfo("gives_state_self", index = true)
    val givesStateSelf: Uuid?,
    @ColumnInfo("gives_state_target", index = true)
    val givesStateTarget: Uuid?
)
@Entity(
    tableName = "feature_regains",
    foreignKeys = [
        ForeignKey(DbFeature::class, ["id"], ["feature_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbFeatureRegain(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("feature_id", index = true) val featureId: Uuid,
    @ColumnInfo("regains_count") val regainsCount: Int?, // how many is regain. null for all
    @ColumnInfo("time_unit") val timeUnit: TimeUnit,
    @ColumnInfo("time_unit_count") val timeUnitCount: Int
)