package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.Spell
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

@Entity(
    tableName = "class_spells",
    foreignKeys = [
        ForeignKey(Spell::class, ["id"], ["spellId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class ClassSpell( // spells that available for class
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val spellId: Uuid,
    @ColumnInfo(index = true) val classId: Uuid,
)

