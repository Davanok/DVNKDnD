package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.database.ListIntAdapter
import com.davanok.dvnkdnd.database.entities.DatabaseImage
import com.davanok.dvnkdnd.database.entities.Proficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDFeat
import com.davanok.dvnkdnd.database.entities.items.DnDItem

@Entity(
    tableName = "character_stats",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterStats(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
    val skill: Skills
)
@Entity(
    tableName = "character_health",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterHealth(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
    val max: Int,
    val current: Int,
    val temp: Int
)
@Entity(
    tableName = "character_spell_slots",
    foreignKeys = [ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE)]
)
data class CharacterSpellSlots(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
    @TypeConverters(ListIntAdapter::class) val usedSpells: List<Int>
)
@Entity(
    tableName = "character_proficiencies",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterProficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
    @ColumnInfo(index = true) val proficiencyId: Long
)
@Entity(
    tableName = "character_feats",
    foreignKeys = [
        ForeignKey(Character::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDFeat::class, ["id"], ["featId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterFeat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
    @ColumnInfo(index = true) val featId: Long
)

@Entity(
    tableName = "character_images",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["characterId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DatabaseImage::class, ["id"], ["imageId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CharacterImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val characterId: Long,
    @ColumnInfo(index = true) val imageId: Long
)

