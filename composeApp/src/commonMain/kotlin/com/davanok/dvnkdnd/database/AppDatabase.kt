package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.character.CharactersDao
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.database.daos.entities.FullEntitiesDao
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterAttributes
import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import com.davanok.dvnkdnd.database.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.database.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityFullDescription
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage

@Database(
    entities = [
        DnDAbility::class,
        DnDAbilityRegain::class,
        DnDProficiency::class,
        DnDSpell::class,
        SpellArea::class,
        SpellAttack::class,
        SpellAttackLevelModifier::class,
        SpellAttackSave::class,
        Character::class,
        CharacterMainEntity::class,
        CharacterAttributes::class,
        CharacterHealth::class,
        CharacterSpellSlots::class,
        CharacterSelectedModifier::class,
        CharacterImage::class,
        CharacterCoins::class,
        CharacterProficiency::class,
        CharacterFeat::class,
        CharacterCustomModifier::class,
        DbCharacterOptionalValues::class,
        DbCharacterItemLink::class,
        DbCharacterNote::class,
        DnDBackground::class,
        DnDBaseEntity::class,
        EntityFullDescription::class,
        EntityImage::class,
        DnDClass::class,
        ClassSpell::class,
        ClassSpellSlots::class,
        DnDFeat::class,
        DnDRace::class,
        EntityProficiency::class,
        EntityAbility::class,
        EntityModifiersGroup::class,
        EntityModifier::class,
        DnDItem::class,
        ItemPropertyLink::class,
        DnDItemProperty::class,
        Armor::class,
        Weapon::class,
        WeaponDamage::class
    ],
    version = 1
)
@TypeConverters(
    MainAdapters::class,
    EnumListAdapters::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
    abstract fun getBaseEntityDao(): BaseEntityDao
    abstract fun getFullEntityDao(): FullEntitiesDao
}
