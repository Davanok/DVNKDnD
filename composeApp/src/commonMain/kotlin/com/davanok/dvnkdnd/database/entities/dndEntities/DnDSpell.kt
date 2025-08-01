package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.AreaTypes
import com.davanok.dvnkdnd.data.model.dndEnums.DamageTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.MagicSchools
import com.davanok.dvnkdnd.data.model.dndEnums.SpellComponents
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Entity(
    tableName = "spells",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDSpell(
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
        ForeignKey(DnDSpell::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
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
        ForeignKey(DnDSpell::class, ["id"], ["spell_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellAttack(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("spell_id", index = true) val spellId: Uuid,
    val damageType: DamageTypes,
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
)

@Serializable
@Entity(
    tableName = "spell_attack_level_modifiers",
    foreignKeys = [
        ForeignKey(SpellAttack::class, ["id"], ["attack_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellAttackLevelModifier(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("attack_id", index = true) val attackId: Uuid,
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
