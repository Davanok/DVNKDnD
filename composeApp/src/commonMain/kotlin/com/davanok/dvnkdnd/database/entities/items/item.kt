package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
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
    val weight: Int?,
)

@Entity(
    tableName = "item_properties",
    primaryKeys = ["item_id", "property_id"],
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDItemProperty::class, ["id"], ["property_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemPropertyLink(
    @ColumnInfo("item_id", index = true) val itemId: Uuid,
    @ColumnInfo("property_id", index = true) val propertyId: Uuid,
)

// properties for items like heavy, two-handed, graze
@Entity(tableName = "properties")
data class DnDItemProperty(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val description: String,
)

@Entity(
    tableName = "armors",
    foreignKeys = [ForeignKey(DnDItem::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class Armor(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("armor_class")
    val armorClass: Int,
    @ColumnInfo("dex_max_modifier")
    val dexMaxModifier: Int?,
    @ColumnInfo("required_strength")
    val requiredStrength: Int?,
    @ColumnInfo("stealth_disadvantage")
    val stealthDisadvantage: Boolean,
)