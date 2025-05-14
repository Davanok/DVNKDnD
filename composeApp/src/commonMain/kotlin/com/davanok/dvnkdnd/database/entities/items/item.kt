package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlinx.serialization.Serializable
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
    val weight: Int?,
)

@Entity(
    tableName = "item_properties",
    primaryKeys = ["item_id", "property_id"],
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(ItemProperty::class, ["id"], ["property_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemPropertyLink(
    @ColumnInfo("item_id") val itemId: Uuid,
    @ColumnInfo("property_id") val propertyId: Uuid,
)

// properties for items like heavy, two-handed, graze
@Serializable
@Entity(tableName = "properties")
data class ItemProperty(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val description: String,
)

@Serializable
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