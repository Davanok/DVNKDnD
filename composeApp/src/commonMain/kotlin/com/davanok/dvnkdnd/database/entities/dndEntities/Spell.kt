package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.AreaTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.DamageTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import com.davanok.dvnkdnd.data.model.dnd_enums.MagicSchools
import com.davanok.dvnkdnd.data.model.dnd_enums.SpellComponents
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Entity(
    tableName = "spells",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Spell(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val school: MagicSchools,
    val level: Int?,
    val castingTime: String,
    val components: List<SpellComponents>,
    val ritual: Boolean,
    val materialComponent: String?,
    val duration: String,
    val concentration: Boolean,
)

@Serializable
@Entity(
    tableName = "spell_areas",
    foreignKeys = [
        ForeignKey(Spell::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellArea(
    @PrimaryKey val id: Uuid = Uuid.random(),
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

@Serializable
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

@Serializable
@Entity(
    tableName = "spell_attack_save",
    foreignKeys = [
        ForeignKey(SpellAttack::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellAttackSave(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val savingThrow: Stats,
    val halfOnSuccess: Boolean,
)
