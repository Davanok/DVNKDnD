package com.davanok.dvnkdnd.domain.usecases.entities

import com.davanok.dvnkdnd.core.CheckingDataStates
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface EntitiesBootstrapper {
    fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates>

    /**
     * @return true if entity downloaded from remote source, false if entity was in local source
     */
    suspend fun checkAndLoadEntity(entityId: Uuid): Result<Boolean>
}