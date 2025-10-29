package com.davanok.dvnkdnd.database.entities.dndEntities.companion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.TimeUnits
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.Uuid

@Entity(
    tableName = "abilities",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDAbility(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("usage_limit_by_level")
    val usageLimitByLevel: List<Int>
)
@Entity(
    tableName = "ability_regain",
    foreignKeys = [
        ForeignKey(DnDAbility::class, ["id"], ["ability_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDAbilityRegain(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("ability_id", index = true) val abilityId: Uuid,
    @ColumnInfo("regains_count") val regainsCount: Int?, // how many is regain. null for all
    @ColumnInfo("time_unit") val timeUnit: TimeUnits,
    @ColumnInfo("time_unit_count") val timeUnitCount: Int
)