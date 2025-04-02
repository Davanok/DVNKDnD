package com.davanok.dvnkdnd.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.data.model.dnd_enums.AreaTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.DamageTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import com.davanok.dvnkdnd.data.model.dnd_enums.MagicSchools
import com.davanok.dvnkdnd.data.model.dnd_enums.SpellComponents
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSubclass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity


class ListSpellComponentAdapter {
    @TypeConverter
    fun toListConverter(value: String) = value.split(';').map { component ->
        SpellComponents.entries.first { it.toString()[0] == component[0] }
    }

    @TypeConverter
    fun toStringConverter(value: List<SpellComponents>) = value.joinToString(";") {
        it.toString().first().toString()
    }
}


@Entity(
    tableName = "spells",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Spell(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
    val school: MagicSchools,
    val level: Int,
    val castingTime: String,
    @TypeConverters(ListSpellComponentAdapter::class) val components: List<SpellComponents>,
    val ritual: Boolean,
    val materialComponent: String?,
    val duration: String,
    val concentration: Boolean,
)

@Entity(
    tableName = "spell_area",
    foreignKeys = [
        ForeignKey(Spell::class, ["id"], ["spellId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellArea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val spellId: Long,
    val range: Int,
    val area: Int,
    val type: AreaTypes,
)

@Entity(
    tableName = "spell_attacks",
    foreignKeys = [
        ForeignKey(Spell::class, ["id"], ["spellId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellAttack(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val spellId: Long,
    val damageType: DamageTypes,
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
)

@Entity(
    tableName = "spell_attack_level_modifiers",
    foreignKeys = [
        ForeignKey(SpellAttack::class, ["id"], ["attackId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellAttackLevelModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val attackId: Long,
    val level: Int,
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
)

@Entity(
    tableName = "spell_attack_save",
    foreignKeys = [
        ForeignKey(SpellAttack::class, ["id"], ["attackId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellAttackSave(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val attackId: Long,
    val savingThrow: Stats,
    val halfOnSuccess: Boolean,
)

@Entity(
    tableName = "spell_classes",
    foreignKeys = [
        ForeignKey(Spell::class, ["id"], ["spellId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDSubclass::class, ["id"], ["subclassId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellClass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val spellId: Long,
    @ColumnInfo(index = true) val classId: Long,
    @ColumnInfo(index = true) val subclassId: Long?,
)
