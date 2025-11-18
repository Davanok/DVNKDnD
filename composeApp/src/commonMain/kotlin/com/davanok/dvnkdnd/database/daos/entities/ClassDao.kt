package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.model.adapters.entities.toClassSpellSlots
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbSpellSlotType
import com.davanok.dvnkdnd.database.model.adapters.entities.toDnDClass
import kotlin.uuid.Uuid

@Dao
interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(cls: DnDClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpells(clsSpells: List<ClassSpell>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpellSlotTypes(spellSlotType: List<DbSpellSlotType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpellSlots(classSpellSlots: List<ClassSpellSlots>)

    @Transaction
    suspend fun insertFullClassSpellSlots(classId: Uuid, slots: List<SpellSlots>) {
        insertSpellSlotTypes(slots.map { it.type.toDbSpellSlotType() }.toSet().toList())
        insertClassSpellSlots(slots.map { it.toClassSpellSlots(classId) })
    }

    @Transaction
    suspend fun insertClassWithSpells(entityId: Uuid, cls: ClassWithSpells) {
        insertClass(cls.toDnDClass(entityId))
        insertClassSpells(cls.spells.map { ClassSpell(classId = entityId, spellId = it) })
        insertFullClassSpellSlots(entityId, cls.slots)
    }
}