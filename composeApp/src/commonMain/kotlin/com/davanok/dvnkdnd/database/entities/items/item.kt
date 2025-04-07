package com.davanok.dvnkdnd.database.entities.items

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.Uuid


@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDItem(
    @PrimaryKey val id: Uuid,
    val cost: Int?, // in copper pieces
    val weight: Int?
)

@Entity(
    tableName = "armors",
    foreignKeys = [ForeignKey(DnDItem::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class Armor(
    @PrimaryKey val id: Uuid,
    val armorClass: Int,
    val dexMaxModifier: Int?,
    val requiredStrength: Int?,
    val stealthDisadvantage: Boolean,
)