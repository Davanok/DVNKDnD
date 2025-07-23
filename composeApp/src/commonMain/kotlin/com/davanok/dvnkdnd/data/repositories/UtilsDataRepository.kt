package com.davanok.dvnkdnd.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

enum class CheckingDataStates {
    LOAD_FROM_DATABASE,
    CHECKING,
    LOADING_DATA,
    UPDATING,
    FINISH,
    ERROR
}
interface UtilsDataRepository {
    fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates>
}