package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave

@Dao
interface SpellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpell(spell: DnDSpell)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellArea(area: SpellArea)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellAttack(attack: SpellAttack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellAttackModifiers(modifiers: List<SpellAttackLevelModifier>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpellAttackSave(save: SpellAttackSave)

    @Transaction
    suspend fun insertFullSpell(spell: FullSpell) {
        insertSpell(spell.toSpell())
        spell.area?.let { insertSpellArea(it) }
        spell.attacks.forEach { insertFullSpellAttack(it) }
    }

    @Transaction
    suspend fun insertFullSpellAttack(attack: FullSpellAttack) {
        insertSpellAttack(attack.toAttack())
        insertSpellAttackModifiers(attack.modifiers)
        attack.save?.let { insertSpellAttackSave(it) }
    }
}