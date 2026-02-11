package com.davanok.dvnkdnd.data.local.db.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageInteractionType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierRollTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.RollOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlin.uuid.Uuid

@Entity(
    tableName = "entity_value_modifiers",
    foreignKeys = [
        ForeignKey(DbEntityModifiersGroup::class, ["id"], ["group_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityValueModifier(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("group_id", index = true) val groupId: Uuid,

    val priority: Int,
    // What are we modifying? (e.g., ATTRIBUTE -> STR)
    @ColumnInfo("target_scope")
    val targetScope: ModifierValueTarget,
    @ColumnInfo("target_key")
    val targetKey: String?, // Enum name stored as string (e.g., "STR", "ATHLETICS")

    // How do we modify it? (ADD, SET)
    val operation: ValueOperation,

    // CALCULATION LOGIC: (Source * Multiplier) + Flat
    // Example: "Add Half Proficiency" -> Source=PB, Multiplier=0.5, Flat=0
    // Example: "Str +2" -> Source=FLAT, Multiplier=0, Flat=2
    @ColumnInfo("source_type")
    val sourceType: ValueSourceType,
    @ColumnInfo("source_key")
    val sourceKey: String?,         // If source is ATTRIBUTE, which one?

    val multiplier: Double = 1.0,   // Double allows for "Half Proficiency" logic
    @ColumnInfo("flat_value")
    val flatValue: Int = 0,

    // Optional: Condition text for UI (e.g., "When wearing no armor")
    val condition: String? = null
)

@Entity(
    tableName = "entity_roll_modifiers",
    foreignKeys = [
        ForeignKey(DbEntityModifiersGroup::class, ["id"], ["group_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityRollModifier(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("group_id", index = true)
    val groupId: Uuid,

    @ColumnInfo("target_scope")
    val targetScope: ModifierRollTarget,    // SAVING_THROW
    @ColumnInfo("target_key")
    val targetKey: String?,                 // "DEXTERITY"

    val operation: RollOperation,           // ADVANTAGE

    // Crucial for Rolls: Conditions are very common
    val condition: String?                  // "Against spells", "While heavily obscured"
)

@Entity(
    tableName = "entity_damage_modifiers",
    foreignKeys = [
        ForeignKey(DbEntityModifiersGroup::class, ["id"], ["group_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityDamageModifier(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("group_id", index = true)
    val groupId: Uuid,

    @ColumnInfo("damage_type")
    val damageType: DamageTypes,            // "FIRE", "SLASHING"
    val interaction: DamageInteractionType, // RESISTANCE, IMMUNITY

    val condition: String?                  // "From non-magical attacks"
)

@Entity(
    tableName = "entity_modifiers_groups",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbEntityModifiersGroup(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("entity_id", index = true)
    val entityId: Uuid,

    val name: String,
    val description: String,

    // LOGIC FOR CHOICES
    // If > 0, the user must select this many modifiers from this feature.
    // If 0, all modifiers in this feature are automatically active.
    @ColumnInfo("selection_limit")
    val selectionLimit: Int = 0
)