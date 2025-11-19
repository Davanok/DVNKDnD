package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackSave
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbSpell
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbSpellArea
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbSpellAttack
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbSpellAttackLevelModifier
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbSpellAttackSave
import kotlin.uuid.Uuid

@Dao
interface SpellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpell(spell: DbSpell)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellArea(area: DbSpellArea)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellAttack(attack: DbSpellAttack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellAttackModifiers(modifiers: List<DbSpellAttackLevelModifier>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellAttackSave(save: DbSpellAttackSave)

    @Transaction
    suspend fun insertFullSpell(entityId: Uuid, spell: FullSpell) {
        insertSpell(spell.toDbSpell(entityId))
        spell.area?.let { insertSpellArea(it.toDbSpellArea(entityId)) }
        spell.attacks.forEach { insertFullSpellAttack(entityId, it) }
    }

    @Transaction
    suspend fun insertFullSpellAttack(spellId: Uuid, attack: FullSpellAttack) {
        insertSpellAttack(attack.toDbSpellAttack(spellId))
        insertSpellAttackModifiers(
            attack.modifiers.map {
                it.toDbSpellAttackLevelModifier(attack.id)
            }
        )
        attack.save?.let { insertSpellAttackSave(it.toDbSpellAttackSave(attack.id)) }
    }
}