package com.davanok.dvnkdnd.database.daos

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toEntityModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toEntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.model.DbFullEntity
import com.davanok.dvnkdnd.database.model.EntityWithSub
import io.github.aakira.napier.Napier
import kotlin.uuid.Uuid

@Dao
interface EntitiesDao : EntityInfoDao {
    @Transaction
    @Query("SELECT * FROM base_entities WHERE id == :entityId")
    suspend fun getFullEntity(entityId: Uuid): DbFullEntity?
    @Transaction
    @Query("SELECT * FROM base_entities WHERE id IN (:entityIds)")
    suspend fun getFullEntities(entityIds: List<Uuid>): List<DbFullEntity>


    @Query("SELECT id, type, name, source FROM base_entities WHERE type == :type AND parent_id == :parentId")
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Uuid? = null): List<DnDEntityMin>


    @Transaction
    @Query("SELECT * FROM base_entities WHERE id IN (:entityIds)")
    suspend fun getEntitiesWithSubList(entityIds: List<Uuid>): List<EntityWithSub>
    @Transaction
    @Query("SELECT * FROM base_entities WHERE type == :type")
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<EntityWithSub>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE type IN (:types)")
    suspend fun getEntitiesWithSubList(vararg types: DnDEntityTypes): List<EntityWithSub>

    @Query("SELECT id FROM base_entities WHERE id IN (:entitiesIds)")
    suspend fun getExistingEntities(entitiesIds: List<Uuid>): List<Uuid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DnDBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiers(modifiers: List<EntityModifierBonus>)

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
        insertClass(cls.toDnDClass())
        insertClassSpells(cls.spells)
        insertClassSpellSlots(cls.slots)
    }

    @Transaction
    suspend fun insertFullSpell(spell: FullSpell) {
        insertSpell(spell.toSpell())
        spell.area?.let {
            insertSpellArea(it)
        }
        spell.attacks.fastForEach {
            insertFullSpellAttack(it)
        }
    }

    @Transaction
    suspend fun insertFullItem(item: FullItem) {
        insertItem(item.toDnDItem())
        insertItemJoinProperties(item.properties)
        item.armor?.let { insertArmor(it) }
        item.weapon?.let { insertFullWeapon(it) }
    }

    @Transaction
    suspend fun insertFullEntity(fullEntity: DnDFullEntity) {
        Napier.i { "insert full entity (${fullEntity.type}) with id: ${fullEntity.id}" }
        val entityId = fullEntity.id
        fullEntity.companionEntities.fastForEach {
            insertFullEntity(it)
        }

        insertEntity(fullEntity.toBaseEntity())
        insertModifiers(fullEntity.modifierBonuses.fastMap { it.toEntityModifierBonus(entityId) })
        insertSkills(fullEntity.skills.fastMap { it.toEntitySkill(entityId) })
        insertSavingThrows(fullEntity.savingThrows)
        fullEntity.selectionLimits?.let { insertSelectionLimits(it) }

        fullEntity.cls?.let { insertClassWithSpells(it) }
        fullEntity.race?.let { insertRace(it) }
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