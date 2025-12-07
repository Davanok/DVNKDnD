package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.domain.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.domain.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbClassSpellSlots
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbSpellSlotType
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbClass
import kotlin.uuid.Uuid

@Dao
interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(cls: DbClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpells(clsSpells: List<DbClassSpell>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellSlotTypes(spellSlotType: List<DbSpellSlotType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpellSlots(classSpellSlots: List<DbClassSpellSlots>)

    @Transaction
    suspend fun insertFullClassSpellSlots(classId: Uuid, slots: List<SpellSlots>) {
        insertSpellSlotTypes(slots.map { it.type.toDbSpellSlotType() }.toSet().toList())
        insertClassSpellSlots(slots.map { it.toDbClassSpellSlots(classId) })
    }

    @Transaction
    suspend fun insertClassWithSpells(entityId: Uuid, cls: ClassWithSpells) {
        insertClass(cls.toDbClass(entityId))
        insertClassSpells(cls.spells.map { DbClassSpell(classId = entityId, spellId = it) })
        insertFullClassSpellSlots(entityId, cls.slots)
    }
}