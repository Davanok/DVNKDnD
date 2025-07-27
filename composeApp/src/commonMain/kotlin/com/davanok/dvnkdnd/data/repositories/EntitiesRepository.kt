package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlin.uuid.Uuid

interface EntitiesRepository {
    suspend fun insertEntity(entity: DnDBaseEntity): Result<Unit>

    suspend fun insertModifiers(modifiers: List<EntityModifierBonus>): Result<Unit>
    suspend fun insertSkills(skills: List<EntitySkill>): Result<Unit>
    suspend fun insertSavingThrows(throws: List<EntitySavingThrow>): Result<Unit>

    suspend fun insertProficiencies(proficiencies: List<EntityProficiency>): Result<Unit>
    suspend fun insertAbilities(abilities: List<EntityAbility>): Result<Unit>

    suspend fun insertSelectionLimits(selectionLimits: EntitySelectionLimits): Result<Unit>

    suspend fun insertFullEntity(fullEntity: DnDFullEntity): Result<Unit>
    suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>): Result<Unit>

    suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>>
    suspend fun getExistsEntity(entityId: Uuid): Result<Boolean>

    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>>
    suspend fun getEntitiesWithSubList(vararg types: DnDEntityTypes): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>>
}