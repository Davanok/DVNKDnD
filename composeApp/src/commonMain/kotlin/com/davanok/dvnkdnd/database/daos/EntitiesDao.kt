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
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.model.EntityWithSub
import io.github.aakira.napier.Napier
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
        Napier.d { "insert class" }
        insertClass(cls.cls)
        Napier.d { "insert class spells" }
        insertClassSpells(cls.spells)
        Napier.d { "insert class spell slots" }
        insertClassSpellSlots(cls.slots)
    }
    @Transaction
    suspend fun insertFullSpell(spell: FullSpell) {
        Napier.d { "insert spell" }
        insertSpell(spell.spell)
        Napier.d { "insert spell area" }
        insertSpellArea(spell.area)
        spell.attacks.fastForEach {
            insertFullSpellAttack(it)
        }
    }
    @Transaction
    suspend fun insertFullItem(item: FullItem) {
        Napier.d { "insert item" }
        insertItem(item.item)
        Napier.d { "insert item property" }
        insertItemJoinProperties(item.properties)
        Napier.d { "insert armor" }
        item.armor?.let { insertArmor(it) }
        item.weapon?.let { insertFullWeapon(it) }
    }

    @Transaction
    suspend fun insertFullEntity(fullEntity: DnDFullEntity) {
        Napier.i { "insert full entity (${fullEntity.type}) with id: ${fullEntity.id}" }
        fullEntity.companionEntities.fastForEach {
            insertFullEntity(it)
        }

        Napier.d { "insert base entity" }
        insertEntity(fullEntity.toBaseEntity())
        Napier.d { "insert modifiers" }
        insertModifiers(fullEntity.modifiers)
        Napier.d { "insert skills" }
        insertSkills(fullEntity.skills)
        Napier.d { "insert saving throws" }
        insertSavingThrows(fullEntity.savingThrows)
        Napier.d { "insert selection limits" }
        fullEntity.selectionLimits?.let { insertSelectionLimits(it) }

        Napier.d { "insert class" }
        fullEntity.cls?.let { insertClassWithSpells(it) }
        Napier.d { "insert race" }
        fullEntity.race?.let { insertRace(it) }
        Napier.d { "insert background" }
        fullEntity.background?.let { insertBackground(it) }
        Napier.d { "insert feat" }
        fullEntity.feat?.let { insertFeat(it) }
        Napier.d { "insert ability" }
        fullEntity.ability?.let { insertAbility(it) }
        Napier.d { "insert proficiency" }
        fullEntity.proficiency?.let { insertProficiency(it) }
        Napier.d { "insert spell" }
        fullEntity.spell?.let { insertFullSpell(it) }
        Napier.d { "insert item" }
        fullEntity.item?.let { insertFullItem(it) }

        insertProficiencies(fullEntity.proficiencies)
        insertAbilities(fullEntity.abilities)
    }

    @Query("SELECT exists(SELECT 1 FROM base_entities WHERE id == :entityId)")
    suspend fun getExistsEntity(entityId: Uuid): Boolean
}