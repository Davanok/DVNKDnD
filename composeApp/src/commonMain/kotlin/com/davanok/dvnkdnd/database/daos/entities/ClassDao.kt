package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import kotlin.uuid.Uuid

@Dao
interface ClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(cls: DnDClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpells(clsSpells: List<ClassSpell>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpellSlots(classSpellSlots: List<ClassSpellSlots>)

    @Transaction
    suspend fun insertClassWithSpells(entityId: Uuid, cls: ClassWithSpells) {
        insertClass(cls.toDnDClass(entityId))
        insertClassSpells(cls.spells.map { ClassSpell(classId = entityId, spellId = it) })
        insertClassSpellSlots(cls.slots.map { it.toClassSpellSlots(entityId) })
    }
}