package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.types.CheckingDataStates
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid


interface UtilsDataRepository {
    fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates>
}