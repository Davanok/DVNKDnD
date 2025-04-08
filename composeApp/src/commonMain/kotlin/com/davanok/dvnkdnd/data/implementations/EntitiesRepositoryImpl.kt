package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlin.uuid.Uuid

class EntitiesRepositoryImpl(
    private val dao: EntitiesDao,
) : EntitiesRepository {
    override suspend fun insertEntity(entity: DnDBaseEntity) =
        dao.insertEntity(entity)

    override suspend fun insertModifiers(modifiers: List<EntityModifier>) =
        dao.insertModifiers(modifiers)

    override suspend fun insertSkills(skills: List<EntitySkill>) =
        dao.insertSkills(skills)

    override suspend fun insertSavingThrows(throws: List<EntitySavingThrow>) =
        dao.insertSavingThrows(throws)

    override suspend fun insertProficiencies(proficiencies: List<EntityProficiency>) =
        dao.insertProficiencies(proficiencies)

    override suspend fun insertAbilities(abilities: List<EntityAbility>) =
        dao.insertAbilities(abilities)

    override suspend fun insertSelectionLimits(selectionLimits: EntitySelectionLimits) =
        dao.insertSelectionLimits(selectionLimits)


    override suspend fun insertFullEntity(fullEntity: DnDEntityFullInfo) =
        dao.insertFullEntity(fullEntity)

    override suspend fun insertFullEntities(fullEntities: List<DnDEntityFullInfo>) =
        fullEntities.fastForEach { insertFullEntity(it) }

    override suspend fun getExistingEntities(entityIds: List<Uuid>): List<Uuid> =
        dao.getExistingEntities(entityIds)

    override suspend fun getExistsEntity(entityId: Uuid): Boolean =
        dao.getExistsEntity(entityId)

    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) =
        dao.getEntitiesWithSubList(type).fastMap { it.toEntityWithSubEntities() }
}