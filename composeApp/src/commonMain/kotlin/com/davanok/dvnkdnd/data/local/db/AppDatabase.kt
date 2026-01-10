package com.davanok.dvnkdnd.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.data.local.db.daos.entities.FullEntitiesDao
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityFullDescription
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttackSave
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.data.local.db.entities.items.DbArmor
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItem
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemProperty
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeapon
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeaponDamage
import com.davanok.dvnkdnd.data.local.adapters.EnumListAdapters
import com.davanok.dvnkdnd.data.local.adapters.MainAdapters
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemActivationsCount
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbStateDuration
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivation
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationCastsSpell
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationRegain
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemEffect
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeaponDamageCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        DbAbility::class,
        DbAbilityRegain::class,
        DbArmor::class,
        DbBackground::class,
        DbBaseEntity::class,
        DbCharacter::class,
        DbCharacterAttributes::class,
        DbCharacterCoins::class,
        DbCharacterCustomModifier::class,
        DbCharacterFeat::class,
        DbCharacterHealth::class,
        DbCharacterImage::class,
        DbCharacterItemLink::class,
        DbCharacterMainEntity::class,
        DbCharacterNote::class,
        DbCharacterOptionalValues::class,
        DbCharacterProficiency::class,
        DbCharacterSelectedModifier::class,
        DbCharacterSpellLink::class,
        DbCharacterUsedSpellSlots::class,
        DbCharacterStateLink::class,
        DbCharacterItemActivationsCount::class,
        DbItemActivationCastsSpell::class,
        DbClass::class,
        DbClassSpell::class,
        DbClassSpellSlots::class,
        DbEntityAbility::class,
        DbEntityFullDescription::class,
        DbEntityImage::class,
        DbEntityModifier::class,
        DbEntityModifiersGroup::class,
        DbEntityProficiency::class,
        DbFeat::class,
        DbItem::class,
        DbItemProperty::class,
        DbItemPropertyLink::class,
        DbRace::class,
        DbProficiency::class,
        DbState::class,
        DbSpell::class,
        DbSpellArea::class,
        DbSpellAttack::class,
        DbSpellAttackLevelModifier::class,
        DbSpellAttackSave::class,
        DbSpellSlotType::class,
        DbWeapon::class,
        DbWeaponDamage::class,
        DbWeaponDamageCondition::class,
        DbItemEffect::class,
        DbItemActivation::class,
        DbItemActivationRegain::class,
        DbStateDuration::class
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

    companion object {
        private val initialExecs = listOf(
            "CREATE UNIQUE INDEX IF NOT EXISTS idx_character_multiclass_unique ON character_used_spell_slots(character_id) WHERE spell_slot_type_id IS NULL"
        )

        fun buildDatabase(builder: Builder<AppDatabase>) = builder
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addCallback(object : Callback() {
                override fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)

                    initialExecs.forEach {
                        connection.execSQL(it)
                    }
                }
            })
            .build()
    }
}
