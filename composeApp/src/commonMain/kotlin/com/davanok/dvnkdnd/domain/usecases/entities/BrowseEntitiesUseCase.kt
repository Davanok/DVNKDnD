package com.davanok.dvnkdnd.domain.usecases.entities

import androidx.paging.PagingData
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import kotlinx.coroutines.flow.Flow

interface BrowseEntitiesUseCase {
    fun getEntitiesMin(type: DnDEntityTypes, query: String?): Flow<PagingData<DnDEntityWithSubEntities>>
}