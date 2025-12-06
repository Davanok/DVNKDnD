package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DbState
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbFeat
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbProficiency
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbRace
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbEntityAbility
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbEntityProficiency
import com.davanok.dvnkdnd.database.model.entities.DbFullEntity
import com.davanok.dvnkdnd.database.model.entities.toDbBaseEntity
import io.github.aakira.napier.Napier
import kotlin.uuid.Uuid

@Dao
interface FullEntitiesDao: EntityInfoDao, EntityAttributesDao {
    @Transaction
    @Query("SELECT * FROM base_entities WHERE id == :entityId")
    suspend fun getFullEntity(entityId: Uuid): DbFullEntity?

    @Transaction
    @Query("SELECT * FROM base_entities WHERE id IN (:entityIds)")
    suspend fun getFullEntities(entityIds: List<Uuid>): List<DbFullEntity>

    @Transaction
    suspend fun insertFullEntity(fullEntity: DnDFullEntity) {
        Napier.i { "insert full entity (${fullEntity.entity.type}) with id: ${fullEntity.entity.id}" }
        val entityId = fullEntity.entity.id
        fullEntity.companionEntities.forEach {
            insertFullEntity(it)
        }
        insertProficiencies(
            fullEntity.proficiencies.map { it.proficiency.toDbProficiency() }
        )

        insertEntity(fullEntity.entity.toDbBaseEntity())

        fullEntity.modifiersGroups.forEach { insertModifiersGroup(entityId, it) }

        fullEntity.cls?.let { insertClassWithSpells(entityId, it) }
        fullEntity.race?.let { insertRace(it.toDbRace(entityId)) }
        fullEntity.background?.let { insertBackground(DbBackground(entityId)) }
        fullEntity.feat?.let { insertFeat(it.toDbFeat(entityId)) }
        fullEntity.ability?.let { insertAbilityInfo(entityId, it) }
        fullEntity.spell?.let { insertFullSpell(entityId, it) }
        fullEntity.item?.let { insertFullItem(entityId, it) }
        fullEntity.state?.let { insertState(DbState(entityId)) }

        insertProficiencyLinks(fullEntity.proficiencies.map { it.toDbEntityProficiency(entityId) })
        insertAbilityLinks(fullEntity.abilities.map { it.toDbEntityAbility(entityId) })
    }
}