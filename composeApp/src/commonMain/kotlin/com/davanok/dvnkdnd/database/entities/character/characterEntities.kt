@file:kotlin.OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.database.MainAdapters
import com.davanok.dvnkdnd.database.entities.Proficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDFeat
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import okio.Path
import kotlin.uuid.Uuid

@Entity(
    tableName = "character_stats",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterStats(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int
)
@Entity(
    tableName = "character_skills",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterSkill(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid,
    val skill: Skills
)
@Entity(
    tableName = "character_health",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterHealth(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid,
    val max: Int,
    val current: Int,
    val temp: Int
)
@Entity(
    tableName = "character_spell_slots",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterSpellSlots(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid,
    @TypeConverters(MainAdapters::class) val usedSpells: List<Int>
)
@Entity(
    tableName = "character_proficiencies",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterProficiency(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid,
    @ColumnInfo(index = true) val proficiencyId: Uuid
)
@Entity(
    tableName = "character_feats",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDFeat::class, ["id"], ["featId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterFeat(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid,
    @ColumnInfo(index = true) val featId: Uuid
)

@Entity(
    tableName = "character_images",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["characterId"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class CharacterImage(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val characterId: Uuid?,
    val path: Path
)

