package com.davanok.dvnkdnd.database.entities.dndEntities.companion

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Entity(
    tableName = "feats",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDFeat(
    @PrimaryKey val id: Uuid,
    val repeatable: Boolean
)