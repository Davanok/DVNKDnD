package com.davanok.dvnkdnd.data.local.db.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import kotlin.uuid.Uuid

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbState::class, ["id"], ["gives_state_passive"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DbState::class, ["id"], ["gives_state_on_use"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class DbItem(
    @PrimaryKey val id: Uuid,
    val cost: Int?, // in copper pieces
    val weight: Int?,
    val attunement: Boolean,
    @ColumnInfo("gives_state_passive", index = true)
    val givesStatePassive: Uuid?,
    @ColumnInfo("gives_state_on_use", index = true)
    val givesStateOnUse: Uuid?
)

@Entity(
    tableName = "item_properties",
    primaryKeys = ["item_id", "property_id"],
    foreignKeys = [
        ForeignKey(DbItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbItemProperty::class, ["id"], ["property_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbItemPropertyLink(
    @ColumnInfo("item_id", index = true) val itemId: Uuid,
    @ColumnInfo("property_id", index = true) val propertyId: Uuid,
)

// properties for items like heavy, two-handed, graze
@Entity(tableName = "properties")
data class DbItemProperty(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("user_id") val userId: Uuid?,
    val name: String,
    val description: String,
)

@Entity(
    tableName = "armors",
    foreignKeys = [ForeignKey(DbItem::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class DbArmor(
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