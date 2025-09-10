package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDFeat
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDProficiency
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDRace
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toEntityAbility
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toEntityProficiency
import com.davanok.dvnkdnd.database.model.DbFullEntity
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
        Napier.i { "insert full entity (${fullEntity.type}) with id: ${fullEntity.id}" }
        val entityId = fullEntity.id
        fullEntity.companionEntities.fastForEach {
            insertFullEntity(it)
        }
        insertProficiencies(
            fullEntity.proficiencies.fastMap { it.proficiency.toDnDProficiency() }
        )

        insertEntity(fullEntity.toBaseEntity())

        fullEntity.modifiers.fastForEach { insertModifiersGroup(entityId, it) }

        fullEntity.cls?.let { insertClassWithSpells(entityId, it) }
        fullEntity.race?.let { insertRace(it.toDnDRace(entityId)) }
//        fullEntity.background?.let { insertBackground(it) }
        fullEntity.feat?.let { insertFeat(it.toDnDFeat(entityId)) }
        fullEntity.ability?.let { insertAbilityInfo(entityId, it) }
        fullEntity.spell?.let { insertFullSpell(entityId, it) }
        fullEntity.item?.let { insertFullItem(entityId, it) }

        insertProficiencyLinks(fullEntity.proficiencies.fastMap { it.toEntityProficiency(entityId) })
        insertAbilityLinks(fullEntity.abilities.fastMap { it.toEntityAbility(entityId) })
    }
}