package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.database.daos.entities.FullEntitiesDao
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClass
import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedSkill
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityFullDescription
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
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
        CharacterClass::class,
        CharacterStats::class,
        CharacterHealth::class,
        CharacterSpellSlots::class,
        CharacterSelectedSkill::class,
        CharacterImage::class,
        CharacterCoins::class,
        CharacterProficiency::class,
        CharacterFeat::class,
        CharacterSelectedModifierBonus::class,
        DnDBackground::class,
        DnDBaseEntity::class,
        EntityFullDescription::class,
        EntityImage::class,
        DnDClass::class,
        ClassSpell::class,
        ClassSpellSlots::class,
        DnDFeat::class,
        DnDRace::class,
        EntityModifierBonus::class,
        EntitySkill::class,
        EntitySavingThrow::class,
        EntityProficiency::class,
        EntityAbility::class,
        EntitySelectionLimits::class,
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
    ListSpellComponentAdapter::class,
    EnumListAdapters::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
    abstract fun getBaseEntityDao(): BaseEntityDao
    abstract fun getFullEntityDao(): FullEntitiesDao
}
