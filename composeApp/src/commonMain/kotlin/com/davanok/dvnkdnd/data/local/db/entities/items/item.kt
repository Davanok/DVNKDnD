package com.davanok.dvnkdnd.data.local.db.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemEffectScope
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemPropertyType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemsRarity
import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import kotlin.uuid.Uuid

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbItem(
    @PrimaryKey val id: Uuid,
    val cost: Int?, // in copper pieces
    val weight: Int?,
    val equippable: Boolean,
    val rarity: ItemsRarity
)

@Entity(
    tableName = "item_effects",
    foreignKeys = [
        ForeignKey(DbItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbItemEffect(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("item_id", index = true)
    val itemId: Uuid,

    val scope: ItemEffectScope,

    @ColumnInfo("gives_state", index = true)
    val givesState: Uuid
)

@Entity(
    tableName = "item_activations",
    foreignKeys = [
        ForeignKey(DbItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbState::class, ["id"], ["gives_state"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbItemActivation(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("item_id", index = true)
    val itemId: Uuid,
    val name: String,
    @ColumnInfo("requires_attunement")
    val requiresAttunement: Boolean,
    @ColumnInfo("gives_state", index = true)
    val givesState: Uuid?,
    val count: Int?
)

@Entity(
    tableName = "item_activation_casts_spell",
    foreignKeys = [
        ForeignKey(DbItemActivation::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbSpell::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbItemActivationCastsSpell(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("spell_id", index = true)
    val spellId: Uuid,
    val level: Int
)

@Entity(
    tableName = "item_activation_regains",
    foreignKeys = [
        ForeignKey(DbItemActivation::class, ["id"], ["activation_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbItemActivationRegain(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("activation_id", index = true) val activationId: Uuid,
    @ColumnInfo("regains_count") val regainsCount: Int?, // how many is regain. null for all
    @ColumnInfo("time_unit") val timeUnit: TimeUnit,
    @ColumnInfo("time_unit_count") val timeUnitCount: Int
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
    val type: ItemPropertyType,
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