@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.Proficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDItem(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val pinned: Boolean = false,
    val cost: Int?, // in copper pieces
    val weight: Int?
)

@Entity(
    tableName = "item_proficiencies",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemProficiency(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val itemId: Uuid,
    @ColumnInfo(index = true) val proficiencyId: Uuid
)