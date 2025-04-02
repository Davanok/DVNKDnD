package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subraces",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DnDSubrace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
    val speed: Int,
    val modifierSelectLimit: Int?,
    val skillsSelectLimit: Int?,
)