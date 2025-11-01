package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import okio.Path
import kotlin.uuid.Uuid

// one to one
@Entity(
    tableName = "character_attributes",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterAttributes(
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
    foreignKeys = [ForeignKey(Character::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterHealth(
    @PrimaryKey val id: Uuid,
    val max: Int, // without constitution bonus
    val current: Int,
    val temp: Int
)

@Entity(
    tableName = "character_spell_slots",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterSpellSlots(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("used_spells")
    val usedSpells: List<Int>, // used spells for every spell level
)

@Entity(
    tableName = "character_selected_modifier",
    primaryKeys = ["character_id", "modifier_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(EntityModifier::class, ["id"], ["modifier_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterSelectedModifier(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("modifier_id", index = true) val modifierId: Uuid
)

// many to many
@Entity(
    tableName = "character_proficiencies",
    primaryKeys = ["character_id", "proficiency_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDProficiency::class, ["id"], ["proficiency_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterProficiency(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("proficiency_id", index = true) val proficiencyId: Uuid,
)

@Entity(
    tableName = "character_feats",
    primaryKeys = ["character_id", "feat_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDFeat::class, ["id"], ["feat_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterFeat(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("feat_id", index = true) val featId: Uuid,
)

@Entity(
    tableName = "character_images",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class CharacterImage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("character_id", index = true) val characterId: Uuid?,
    val path: Path,
)

@Entity(
    tableName = "character_coins",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterCoins(
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
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterCustomModifier(
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
        ForeignKey(Character::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterOptionalValues( // if value is null -> calculate
    @PrimaryKey val id: Uuid,
    val initiative: Int?,
    @ColumnInfo("armor_class") val armorClass: Int?
)

@Entity(
    tableName = "character_item",
    primaryKeys = ["character_id", "item_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDItem::class, ["id"], ["item_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterItemLink(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("item_id", index = true) val itemId: Uuid,
    val equipped: Boolean,
    val attuned: Boolean
)

@Entity(
    tableName = "character_notes",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterNote(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    val tags: String,
    val text: String,
)