package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.davanok.dvnkdnd.database.daos.character.CharactersDao
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.database.daos.entities.FullEntitiesDao
import com.davanok.dvnkdnd.database.entities.character.DbCharacter
import com.davanok.dvnkdnd.database.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCustomModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.database.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.database.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.database.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.database.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.database.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.database.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityFullDescription
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.DbState
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.database.entities.items.DbArmor
import com.davanok.dvnkdnd.database.entities.items.DbItem
import com.davanok.dvnkdnd.database.entities.items.DbItemProperty
import com.davanok.dvnkdnd.database.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.DbWeapon
import com.davanok.dvnkdnd.database.entities.items.DbWeaponDamage
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
        DbWeaponDamage::class
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
