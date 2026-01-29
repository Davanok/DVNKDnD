package com.davanok.dvnkdnd.data.local.db.entities.character

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
    tableName = "character_custom_value_modifier",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterCustomValueModifier(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("character_id", index = true)
    val characterId: Uuid,

    val name: String,
    val description: String,

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
    tableName = "character_custom_roll_modifier",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterCustomRollModifier(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("character_id", index = true)
    val characterId: Uuid,

    val name: String,
    val description: String,

    @ColumnInfo("target_scope")
    val targetScope: ModifierRollTarget,    // SAVING_THROW
    @ColumnInfo("target_key")
    val targetKey: String?,                 // "DEXTERITY"

    val operation: RollOperation,           // ADVANTAGE

    // Optional: Condition text for UI (e.g., "When wearing no armor")
    val condition: String? = null
)

@Entity(
    tableName = "character_custom_damage_modifier",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterCustomDamageModifier(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("character_id", index = true)
    val characterId: Uuid,

    val name: String,
    val description: String,

    @ColumnInfo("damage_type")
    val damageType: DamageTypes,            // "FIRE", "SLASHING"
    val interaction: DamageInteractionType, // RESISTANCE, IMMUNITY

    // Optional: Condition text for UI (e.g., "When wearing no armor")
    val condition: String? = null
)