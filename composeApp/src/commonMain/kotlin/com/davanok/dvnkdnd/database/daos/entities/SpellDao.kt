package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellArea
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellAttackLevelModifier
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import kotlin.uuid.Uuid

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
    suspend fun insertFullSpell(entityId: Uuid, spell: FullSpell) {
        insertSpell(spell.toSpell(entityId))
        spell.area?.let { insertSpellArea(it.toSpellArea(entityId)) }
        spell.attacks.forEach { insertFullSpellAttack(entityId, it) }
    }

    @Transaction
    suspend fun insertFullSpellAttack(spellId: Uuid, attack: FullSpellAttack) {
        insertSpellAttack(attack.toAttack(spellId))
        insertSpellAttackModifiers(
            attack.modifiers.fastMap {
                it.toSpellAttackLevelModifier(attack.id)
            }
        )
        attack.save?.let { insertSpellAttackSave(it.toSpellAttackSave(attack.id)) }
    }
}