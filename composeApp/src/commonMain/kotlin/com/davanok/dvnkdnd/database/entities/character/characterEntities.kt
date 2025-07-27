package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import okio.Path
import kotlin.uuid.Uuid

// one to one
@Entity(
    tableName = "character_stats",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterStats(
    @PrimaryKey val id: Uuid = Uuid.random(),
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    val max: Int,
    val current: Int,
    val temp: Int,
)

@Entity(
    tableName = "character_spell_slots",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterSpellSlots(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val usedSpells: List<Int>, // used spells for every spell level
)

@Entity(
    tableName = "character_selected_modifier_bonuses",
    primaryKeys = ["character_id", "bonus_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(EntityModifierBonus::class, ["id"], ["bonus_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterSelectedModifierBonus(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("bonus_id", index = true) val bonusId: Uuid,
)

// one to many
@Entity(
    tableName = "character_selected_skills",
    primaryKeys = ["character_id", "skill_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(EntitySkill::class, ["id"], ["skill_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterSelectedSkill(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("skill_id", index = true) val skillId: Uuid
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