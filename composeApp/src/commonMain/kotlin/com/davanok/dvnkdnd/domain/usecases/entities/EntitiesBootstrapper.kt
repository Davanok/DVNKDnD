package com.davanok.dvnkdnd.domain.usecases.entities

import com.davanok.dvnkdnd.core.CheckingDataStates
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface EntitiesBootstrapper {
    fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates>

    suspend fun checkAndLoadEntity(entityId: Uuid): Result<Unit>
}