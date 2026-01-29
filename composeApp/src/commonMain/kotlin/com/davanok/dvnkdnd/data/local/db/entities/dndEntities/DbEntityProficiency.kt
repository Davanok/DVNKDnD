package com.davanok.dvnkdnd.data.local.db.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import kotlin.uuid.Uuid

@Entity(
    tableName = "entity_proficiencies",
    primaryKeys = ["entity_id", "proficiency_id"],
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbProficiency::class, ["id"], ["proficiency_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityProficiency(
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    @ColumnInfo("proficiency_id", index = true) val proficiencyId: Uuid,
    val level: Int
)