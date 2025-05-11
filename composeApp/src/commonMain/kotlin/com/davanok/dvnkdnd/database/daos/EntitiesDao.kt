package com.davanok.dvnkdnd.database.daos

import androidx.compose.ui.util.fastForEach
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.FullItem
import com.davanok.dvnkdnd.data.model.entities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.RaceWithSizes
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.model.EntityWithSub
import kotlin.uuid.Uuid

@Dao
interface EntitiesDao: EntityInfoDao {
    @Query("SELECT id, type, name, source FROM base_entities WHERE type == :type AND parent_id == :parentId")
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Uuid? = null): List<DnDEntityMin>
    @Transaction
    @Query("SELECT * FROM base_entities WHERE type == :type")
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<EntityWithSub>

    @Query("SELECT id FROM base_entities WHERE id IN (:entitiesIds)")
    suspend fun getExistingEntities(entitiesIds: List<Uuid>): List<Uuid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DnDBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiers(modifiers: List<EntityModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<EntitySkill>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingThrows(throws: List<EntitySavingThrow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencies(proficiency: List<EntityProficiency>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbilities(ability: List<EntityAbility>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectionLimits(selectionLimit: EntitySelectionLimits)

    @Transaction
    suspend fun insertClassWithSpells(cls: ClassWithSpells) {
        insertClass(cls.cls)
        insertClassSpells(cls.spells)
        insertClassSpellSlots(cls.slots)
    }
    @Transaction
    suspend fun insertRaceWithSizes(race: RaceWithSizes) {
        insertRace(race.race)
        insertRaceSizes(race.sizes)
    }
    @Transaction
    suspend fun insertFullSpell(spell: FullSpell) {
        insertSpell(spell.spell)
        insertSpellArea(spell.area)
        spell.attacks.fastForEach {
            insertFullSpellAttack(it)
        }
    }
    @Transaction
    suspend fun insertFullItem(item: FullItem) {
        insertItem(item.item)
        insertItemJoinProperties(item.properties)
        item.armor?.let { insertArmor(it) }
        item.weapon?.let { insertFullWeapon(it) }
    }

    @Transaction
    suspend fun insertFullEntity(fullEntity: DnDFullEntity) {
        fullEntity.companionEntities.fastForEach {
            insertFullEntity(it)
        }

        insertEntity(fullEntity.toBaseEntity())
        insertModifiers(fullEntity.modifiers)
        insertSkills(fullEntity.skills)
        insertSavingThrows(fullEntity.savingThrows)
        fullEntity.selectionLimits?.let { insertSelectionLimits(it) }

        fullEntity.cls?.let { insertClassWithSpells(it) }
        fullEntity.race?.let { insertRaceWithSizes(it) }
        fullEntity.background?.let { insertBackground(it) }
        fullEntity.feat?.let { insertFeat(it) }
        fullEntity.ability?.let { insertAbility(it) }
        fullEntity.proficiency?.let { insertProficiency(it) }
        fullEntity.spell?.let { insertFullSpell(it) }
        fullEntity.item?.let { insertFullItem(it) }

        insertProficiencies(fullEntity.proficiencies)
        insertAbilities(fullEntity.abilities)
    }

    @Query("SELECT exists(SELECT 1 FROM base_entities WHERE id == :entityId)")
    suspend fun getExistsEntity(entityId: Uuid): Boolean
}