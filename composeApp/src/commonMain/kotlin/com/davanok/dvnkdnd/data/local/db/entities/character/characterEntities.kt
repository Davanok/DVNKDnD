package com.davanok.dvnkdnd.data.local.db.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItem
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import kotlin.uuid.Uuid

// one to one
@Entity(
    tableName = "character_attributes",
    foreignKeys = [ForeignKey(DbCharacter::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class DbCharacterAttributes(
    @PrimaryKey val id: Uuid,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
)

@Entity(
    tableName = "character_health",
    foreignKeys = [ForeignKey(DbCharacter::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class DbCharacterHealth(
    @PrimaryKey val id: Uuid,
    val max: Int, // without constitution bonus
    val current: Int,
    val temp: Int
)

@Entity(
    tableName = "character_used_spell_slots",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbSpellSlotType::class, ["id"], ["spell_slot_type_id"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index(value = ["character_id", "spell_slot_type_id"], unique = true)]
)
data class DbCharacterUsedSpellSlots(
    @PrimaryKey
    val id: Uuid = Uuid.random(),
    @ColumnInfo("character_id", index = true)
    val characterId: Uuid,
    @ColumnInfo("spell_slot_type_id", index = true)
    val spellSlotTypeId: Uuid?,
    @ColumnInfo("used_spells")
    val usedSpells: List<Int>, // used spells for every spell level
)

@Entity(
    tableName = "character_selected_modifier",
    primaryKeys = ["character_id", "modifier_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbEntityModifier::class, ["id"], ["modifier_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterSelectedModifier(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("modifier_id", index = true) val modifierId: Uuid
)

// many to many
@Entity(
    tableName = "character_proficiencies",
    primaryKeys = ["character_id", "proficiency_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbProficiency::class, ["id"], ["proficiency_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterProficiency(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("proficiency_id", index = true) val proficiencyId: Uuid,
)

@Entity(
    tableName = "character_feats",
    primaryKeys = ["character_id", "feat_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbFeat::class, ["id"], ["feat_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterFeat(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("feat_id", index = true) val featId: Uuid,
)

@Entity(
    tableName = "character_images",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class DbCharacterImage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("character_id", index = true) val characterId: Uuid?,
    val path: String,
)

@Entity(
    tableName = "character_coins",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterCoins(
    @PrimaryKey
    @ColumnInfo("character_id")
    val characterId: Uuid,
    val copper: Int,
    val silver: Int,
    val electrum: Int,
    val gold: Int,
    val platinum: Int
)

@Entity(
    tableName = "character_custom_modifiers",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterCustomModifier(
    @PrimaryKey
    val id: Uuid,
    @ColumnInfo("character_id", index = true)
    val characterId: Uuid,

    @ColumnInfo("target_global")
    val targetGlobal: DnDModifierTargetType,
    val operation: DnDModifierOperation,
    @ColumnInfo("value_source")
    val valueSource: DnDModifierValueSource,
    @ColumnInfo("value_source_target")
    val valueSourceTarget: String?,

    val name: String,
    val description: String?,
    @ColumnInfo("selection_limit") val selectionLimit: Int,
    val priority: Int,

    val target: String,
    val value: Double
)

@Entity(
    tableName = "character_optional_values",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterOptionalValues( // if value is null: calculate
    @PrimaryKey val id: Uuid,
    @ColumnInfo("proficiency_bonus") val proficiencyBonus: Int?,
    val initiative: Int?,
    @ColumnInfo("armor_class") val armorClass: Int?
)

@Entity(
    tableName = "character_items",
    primaryKeys = ["character_id", "item_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterItemLink(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("item_id", index = true) val itemId: Uuid,
    val equipped: Boolean,
    val active: Boolean,
    val attuned: Boolean,
    val count: Int?
)
@Entity(
    tableName = "character_item_activation_count",
    primaryKeys = ["character_id", "activation_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbItemActivation::class, ["id"], ["activation_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterItemActivationsCount(
    @ColumnInfo("character_id", index = true)
    val characterId: Uuid,
    @ColumnInfo("activation_id", index = true)
    val activationId: Uuid,
    val count: Int
)
@Entity(
    tableName = "character_spell_links",
    primaryKeys = ["character_id", "spell_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbSpell::class, ["id"], ["spell_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterSpellLink(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("spell_id", index = true) val spellId: Uuid,
    val ready: Boolean
)

@Entity(
    tableName = "character_notes",
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterNote(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    val pinned: Boolean,
    val tags: String,
    val text: String,
)

@Entity(
    tableName = "character_states",
    primaryKeys = ["character_id", "state_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbState::class, ["id"], ["state_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbBaseEntity::class, ["id"], ["from_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class DbCharacterStateLink(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("state_id", index = true) val stateId: Uuid,
    @ColumnInfo("from_id", index = true) val fromId: Uuid?
)