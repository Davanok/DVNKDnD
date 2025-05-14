package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.items.DnDItem
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

// one to many
@Entity(
    tableName = "character_skills",
    foreignKeys = [ForeignKey(
        Character::class,
        ["id"],
        ["character_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CharacterSkill(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    val skill: Skills,
)

@Entity(
    tableName = "character_images",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["character_id"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class CharacterImage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("character_id", index = true) val characterId: Uuid?,
    val path: Path,
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
    tableName = "character_selected_modifiers",
    primaryKeys = ["character_id", "modifier_id"],
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(EntityModifier::class, ["id"], ["modifier_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterSelectedModifiers(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("modifier_id", index = true) val modifierId: Uuid,
)

