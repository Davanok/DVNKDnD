package com.davanok.dvnkdnd.database.entities.dndEntities.concept

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.Spell
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Entity(
    tableName = "classes",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDClass( // also subclass
    @PrimaryKey val id: Uuid, // in same time is entity id
    val mainStat: Stats,
    val hitDice: Dices,
)
@Serializable
@Entity(
    tableName = "class_spells",
    primaryKeys = ["classId", "spellId"],
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Spell::class, ["id"], ["spellId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class ClassSpell( // spells that available for class
    @ColumnInfo(index = true) val classId: Uuid,
    @ColumnInfo(index = true) val spellId: Uuid,
)
@Serializable
@Entity(
    tableName = "spell_slots",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class ClassSpellSlot(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @SerialName("class_id")
    @ColumnInfo(index = true) val classId: Uuid,
    val level: Int,
    @SerialName("prepared_spells")
    val preparedSpells: Int?,
    val cantrips: Int?,
    @SerialName("spell_slots")
    val spellSlots: List<Int>
)

