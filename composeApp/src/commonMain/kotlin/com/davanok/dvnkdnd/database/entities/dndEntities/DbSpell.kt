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
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.CastingTime
import kotlin.uuid.Uuid


@Entity(
    tableName = "spells",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbState::class, ["id"], ["gives_state"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class DbSpell(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val school: MagicSchools,
    val level: Int,
    @ColumnInfo("casting_time")
    val castingTime: CastingTime,
    @ColumnInfo("casting_time_other")
    val castingTimeOther: String?,
    val components: List<SpellComponents>,
    val ritual: Boolean,
    @ColumnInfo("material_component")
    val materialComponent: String?,
    val duration: String,
    val concentration: Boolean
)

@Entity(
    tableName = "spell_areas",
    foreignKeys = [
        ForeignKey(DbSpell::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbSpellArea(
    @PrimaryKey val id: Uuid,
    val range: Int,
    val type: AreaTypes,
    val width: Int,
    val height: Int
)

@Entity(
    tableName = "spell_attacks",
    foreignKeys = [
        ForeignKey(DbSpell::class, ["id"], ["spell_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbSpellAttack(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("spell_id", index = true) val spellId: Uuid,
    val damageType: DamageTypes,
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
    @ColumnInfo("gives_state", index = true)
    val givesState: Uuid?
)

@Entity(
    tableName = "spell_attack_level_modifiers",
    foreignKeys = [
        ForeignKey(DbSpellAttack::class, ["id"], ["attack_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbSpellAttackLevelModifier(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("attack_id", index = true) val attackId: Uuid,
    val level: Int,
    @ColumnInfo("dice_count") val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
)

@Entity(
    tableName = "spell_attack_save",
    foreignKeys = [
        ForeignKey(DbSpellAttack::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbSpellAttackSave(
    @PrimaryKey val id: Uuid,
    @ColumnInfo("saving_throw") val savingThrow: Attributes,
    @ColumnInfo("half_on_success") val halfOnSuccess: Boolean,
)
