package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "armors",
    foreignKeys = [ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE)]
)
data class Armor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val itemId: Long,
    val armorClass: Int,
    val dexMaxModifier: Int,
    val requiredStrength: Int?,
    val stealthDisadvantage: Boolean,
)
