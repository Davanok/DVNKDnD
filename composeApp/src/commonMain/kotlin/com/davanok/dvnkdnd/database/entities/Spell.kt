@file:kotlin.OptIn(kotlin.uuid.ExperimentalUuidApi::class)

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
import kotlin.uuid.Uuid


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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val spellId: Uuid,
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val spellId: Uuid,
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val attackId: Uuid,
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val attackId: Uuid,
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
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val spellId: Uuid,
    @ColumnInfo(index = true) val classId: Uuid,
    @ColumnInfo(index = true) val subclassId: Uuid?,
)
