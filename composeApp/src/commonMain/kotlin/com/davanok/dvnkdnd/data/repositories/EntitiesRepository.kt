package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlin.uuid.Uuid

interface EntitiesRepository {
    suspend fun insertEntity(entity: DnDBaseEntity)

    suspend fun insertModifiers(modifiers: List<EntityModifier>)
    suspend fun insertSkills(skills: List<EntitySkill>)
    suspend fun insertSavingThrows(throws: List<EntitySavingThrow>)

    suspend fun insertProficiencies(proficiencies: List<EntityProficiency>)
    suspend fun insertAbilities(abilities: List<EntityAbility>)

    suspend fun insertSelectionLimits(selectionLimits: EntitySelectionLimits)

    suspend fun insertFullEntity(fullEntity: DnDEntityFullInfo)
    suspend fun insertFullEntities(fullEntities: List<DnDEntityFullInfo>)

    /**
     * @param entityIds list of uuid that checked for existing in db
     * @return list of uuid that exists in db
     */
    suspend fun getExistingEntities(entityIds: List<Uuid>): List<Uuid>
    suspend fun getExistsEntity(entityId: Uuid): Boolean

    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<DnDEntityWithSubEntities>
}