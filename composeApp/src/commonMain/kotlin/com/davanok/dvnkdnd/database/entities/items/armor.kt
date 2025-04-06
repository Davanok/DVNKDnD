@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Entity(
    tableName = "armors",
    foreignKeys = [ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE)]
)
data class Armor(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val itemId: Uuid,
    val armorClass: Int,
    val dexMaxModifier: Int,
    val requiredStrength: Int?,
    val stealthDisadvantage: Boolean,
)
