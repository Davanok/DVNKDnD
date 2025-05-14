package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.Spell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClasses
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.CharacterSkill
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifiers
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityFullDescription
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlot
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage

@Database(
    entities = [
        DnDAbility::class,
        DnDProficiency::class,
        Spell::class,
        SpellArea::class,
        SpellAttack::class,
        SpellAttackLevelModifier::class,
        SpellAttackSave::class,
        Character::class,
        CharacterClasses::class,
        CharacterStats::class,
        CharacterHealth::class,
        CharacterSpellSlots::class,
        CharacterSkill::class,
        CharacterImage::class,
        CharacterProficiency::class,
        CharacterFeat::class,
        CharacterSelectedModifiers::class,
        DnDBackground::class,
        DnDBaseEntity::class,
        EntityFullDescription::class,
        EntityImage::class,
        DnDClass::class,
        ClassSpell::class,
        ClassSpellSlot::class,
        DnDFeat::class,
        DnDRace::class,
        EntityModifier::class,
        EntitySkill::class,
        EntitySavingThrow::class,
        EntityProficiency::class,
        EntityAbility::class,
        EntitySelectionLimits::class,
        DnDItem::class,
        ItemPropertyLink::class,
        ItemProperty::class,
        Armor::class,
        Weapon::class,
        WeaponDamage::class
    ],
    version = 1
)
@TypeConverters(MainAdapters::class, ListSpellComponentAdapter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
    abstract fun getEntitiesDao(): EntitiesDao
}
