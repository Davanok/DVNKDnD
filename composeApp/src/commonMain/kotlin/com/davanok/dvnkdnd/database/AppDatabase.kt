package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.daos.ProficienciesDao
import com.davanok.dvnkdnd.database.entities.ListSpellComponentAdapter
import com.davanok.dvnkdnd.database.entities.DnDProficiency
import com.davanok.dvnkdnd.database.entities.Spell
import com.davanok.dvnkdnd.database.entities.SpellArea
import com.davanok.dvnkdnd.database.entities.SpellAttack
import com.davanok.dvnkdnd.database.entities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClasses
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.character.CharacterSkill
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityFullDescription
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.RaceSize
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import com.davanok.dvnkdnd.database.entities.items.WeaponProperties

@Database(
    entities = [
        DnDProficiency::class,
        Spell::class,
        SpellArea::class,
        SpellAttack::class,
        SpellAttackLevelModifier::class,
        SpellAttackSave::class,
        Character::class,
        CharacterClasses::class,
        CharacterStats::class,
        CharacterSkill::class,
        CharacterHealth::class,
        CharacterSpellSlots::class,
        CharacterProficiency::class,
        CharacterFeat::class,
        CharacterImage::class,
        DnDAbility::class,
        DnDBaseEntity::class,
        EntityFullDescription::class,
        EntityImage::class,
        DnDClass::class,
        ClassSpell::class,
        DnDFeat::class,
        DnDRace::class,
        RaceSize::class,
        EntityModifier::class,
        EntitySkill::class,
        EntitySavingThrow::class,
        EntityProficiency::class,
        EntityAbility::class,
        EntitySelectionLimits::class,
        SpellSlots::class,
        DnDItem::class,
        Armor::class,
        ItemProperty::class,
        Weapon::class,
        WeaponDamage::class,
        WeaponProperties::class
               ],
    version = 1
)
@TypeConverters(MainAdapters::class, ListSpellComponentAdapter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
    abstract fun getProficienciesDao(): ProficienciesDao
    abstract fun getEntitiesDao(): EntitiesDao
}


