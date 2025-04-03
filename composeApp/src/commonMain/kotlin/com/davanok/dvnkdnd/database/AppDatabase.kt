package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.ClassesDao
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.daos.ProficienciesDao
import com.davanok.dvnkdnd.database.entities.ListSpellComponentAdapter
import com.davanok.dvnkdnd.database.entities.Proficiency
import com.davanok.dvnkdnd.database.entities.Spell
import com.davanok.dvnkdnd.database.entities.SpellArea
import com.davanok.dvnkdnd.database.entities.SpellAttack
import com.davanok.dvnkdnd.database.entities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.SpellClass
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.character.CharacterSkill
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.FeatModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.FeatSkill
import com.davanok.dvnkdnd.database.entities.dndEntities.FeatProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.RaceSize
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSubclass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSubrace
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiencies
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityFullDescription
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProficiency
import com.davanok.dvnkdnd.database.entities.items.Property
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import com.davanok.dvnkdnd.database.entities.items.WeaponProperties

@Database(
    entities = [
        Proficiency::class,
        Spell::class,
        SpellArea::class,
        SpellAttack::class,
        SpellAttackLevelModifier::class,
        SpellAttackSave::class,
        SpellClass::class,
        Character::class,
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
        DnDFeat::class,
        FeatModifier::class,
        FeatSkill::class,
        FeatProficiency::class,
        DnDRace::class,
        RaceSize::class,
        DnDSubclass::class,
        DnDSubrace::class,
        EntitySkill::class,
        EntitySavingThrow::class,
        EntityProficiencies::class,
        EntityAbility::class,
        EntityModifier::class,
        SpellSlots::class,
        Armor::class,
        DnDItem::class,
        ItemProficiency::class,
        Property::class,
        Weapon::class,
        WeaponDamage::class,
        WeaponProperties::class
               ],
    version = 1
)
@TypeConverters(ListIntAdapter::class, ListSpellComponentAdapter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
    abstract fun getClassesDao(): ClassesDao
    abstract fun getProficienciesDao(): ProficienciesDao
    abstract fun getEntitiesDao(): EntitiesDao
}


