package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.Proficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity


@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
    val pinned: Boolean = false,
    val cost: Int?, // in copper pieces
    val weight: Int?,
    val count: Int
)

@Entity(
    tableName = "item_proficiencies",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemProficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val itemId: Long,
    @ColumnInfo(index = true) val proficiencyId: Long
)