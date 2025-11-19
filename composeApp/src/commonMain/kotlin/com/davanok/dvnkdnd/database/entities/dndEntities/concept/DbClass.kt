package com.davanok.dvnkdnd.database.entities.dndEntities.concept

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.CasterProgression
import com.davanok.dvnkdnd.data.model.dndEnums.TimeUnits
import com.davanok.dvnkdnd.database.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpell
import kotlin.uuid.Uuid

@Entity(
    tableName = "classes",
    foreignKeys = [
        ForeignKey(DbBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbClass( // also subclass
    @PrimaryKey val id: Uuid, // in same time is entity id
    val primaryStats: List<Attributes>,
    val hitDice: Dices,
    val caster: CasterProgression
)
@Entity(
    tableName = "class_spells",
    primaryKeys = ["class_id", "spell_id"],
    foreignKeys = [
        ForeignKey(DbClass::class, ["id"], ["class_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbSpell::class, ["id"], ["spell_id"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DbClassSpell( // spells that available for class
    @ColumnInfo("class_id", index = true) val classId: Uuid,
    @ColumnInfo("spell_id", index = true) val spellId: Uuid,
)
@Entity(
    tableName = "spell_slots",
    indices = [Index(value = ["class_id", "type_id", "level"], unique = true)],
    foreignKeys = [
        ForeignKey(DbClass::class, ["id"], ["class_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbSpellSlotType::class, ["id"], ["type_id"], onDelete = ForeignKey.SET_DEFAULT)
    ]
)
data class DbClassSpellSlots(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("class_id", index = true) val classId: Uuid,
    @ColumnInfo("type_id", index = true, defaultValue = "'00000000-0000-0000-0000-000000000000'")
    val typeId: Uuid,
    val level: Int,
    val preparedSpells: Int?,
    val cantrips: Int?,
    @ColumnInfo("spell_slots")
    val spellSlots: List<Int>
)
@Entity(tableName = "spell_slot_types")
data class DbSpellSlotType(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("user_id") val userId: Uuid? = null,
    val name: String,
    val regain: TimeUnits?,
    val spell: Boolean
)

