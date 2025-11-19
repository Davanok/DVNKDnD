package com.davanok.dvnkdnd.database.entities.dndEntities.companion

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DbBaseEntity
import kotlin.uuid.Uuid

@Entity(
    tableName = "feats",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbFeat(
    @PrimaryKey val id: Uuid,
    val repeatable: Boolean
)