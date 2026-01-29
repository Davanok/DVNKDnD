package com.davanok.dvnkdnd.data.local.db.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeature
import kotlin.uuid.Uuid


@Entity(
    tableName = "entity_features",
    primaryKeys = ["entity_id", "feature_id"],
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbFeature::class, ["id"], ["feature_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityFeature(
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    @ColumnInfo("feature_id", index = true) val featureId: Uuid,
    val level: Int
)