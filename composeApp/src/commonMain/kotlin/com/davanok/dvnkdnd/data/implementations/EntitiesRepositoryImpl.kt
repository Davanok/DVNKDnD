package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.DnDFullEntity
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlin.uuid.Uuid

class EntitiesRepositoryImpl(
    private val dao: EntitiesDao,
) : EntitiesRepository {
    override suspend fun insertEntity(entity: DnDBaseEntity) = runCatching {
        dao.insertEntity(entity)
    }

    override suspend fun insertModifiers(modifiers: List<EntityModifierBonus>) = runCatching {
        dao.insertModifiers(modifiers)
    }

    override suspend fun insertSkills(skills: List<EntitySkill>) = runCatching {
        dao.insertSkills(skills)
    }

    override suspend fun insertSavingThrows(throws: List<EntitySavingThrow>) = runCatching {
        dao.insertSavingThrows(throws)
    }

    override suspend fun insertProficiencies(proficiencies: List<EntityProficiency>) = runCatching {
        dao.insertProficiencies(proficiencies)
    }

    override suspend fun insertAbilities(abilities: List<EntityAbility>) = runCatching {
        dao.insertAbilities(abilities)
    }

    override suspend fun insertSelectionLimits(selectionLimits: EntitySelectionLimits) =
        runCatching {
            dao.insertSelectionLimits(selectionLimits)
        }


    override suspend fun insertFullEntity(fullEntity: DnDFullEntity) = runCatching {
        dao.insertFullEntity(fullEntity)
    }

    override suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>) = runCatching {
        fullEntities.partition { it.parentId == null }.let { (withoutParent, withParent) ->
            withoutParent.fastForEach { insertFullEntity(it) }
            withParent.fastForEach { insertFullEntity(it) }
        }
        // false is upper then true
    }

    override suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>> =
        runCatching {
            dao.getExistingEntities(entityIds)
        }

    override suspend fun getExistsEntity(entityId: Uuid): Result<Boolean> = runCatching {
        dao.getExistsEntity(entityId)
    }

    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) = runCatching {
        dao.getEntitiesWithSubList(type).fastMap { it.toEntityWithSubEntities() }
    }

    override suspend fun getEntitiesWithSubList(
        vararg types: DnDEntityTypes
    ): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>> = runCatching {
        dao.getEntitiesWithSubList(*types).groupBy { it.type }
    }
}