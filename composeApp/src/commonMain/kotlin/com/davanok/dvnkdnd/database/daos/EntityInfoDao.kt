package com.davanok.dvnkdnd.database.daos

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlot
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage

@Dao
interface EntityInfoDao {
    // class
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(cls: DnDClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpells(clsSpells: List<ClassSpell>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSpellSlots(classSpellSlots: List<ClassSpellSlot>)

    // race
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: DnDRace)

    // background
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackground(background: DnDBackground)

    // feat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeat(feat: DnDFeat)

    // ability
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbility(ability: DnDAbility)

    // proficiency
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiency(proficiency: DnDProficiency)

    // spell
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
    suspend fun insertFullSpellAttack(attack: FullSpellAttack) {
        insertSpellAttack(attack.toAttack())
        insertSpellAttackModifiers(attack.modifiers)
        attack.save?.let { insertSpellAttackSave(it) }
    }

    // item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DnDItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPropertyLinks(link: List<ItemPropertyLink>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemProperties(properties: List<ItemProperty>)

    @Transaction
    suspend fun insertItemJoinProperties(properties: List<JoinItemProperty>) {
        insertItemProperties(properties.fastMap { it.property })
        insertItemPropertyLinks(properties.fastMap { it.toItemPropertyLink() })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArmor(armor: Armor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapon(weapon: Weapon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeaponDamages(damages: List<WeaponDamage>)

    @Transaction
    suspend fun insertFullWeapon(weapon: FullWeapon) {
        insertWeapon(weapon.toWeapon())
        insertWeaponDamages(weapon.damages)
    }
}