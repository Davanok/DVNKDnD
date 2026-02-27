package com.davanok.dvnkdnd.data.usecase.implementations

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.davanok.dvnkdnd.core.DnDEntityMinPagingSource
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import com.davanok.dvnkdnd.domain.usecases.entities.BrowseEntitiesUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class BrowseEntitiesUseCaseImpl(
    val browseRepository: BrowseRepository
): BrowseEntitiesUseCase {
    override fun getEntitiesMin(
        type: DnDEntityTypes,
        query: String?
    ): Flow<PagingData<DnDEntityWithSubEntities>> =
        Pager(PagingConfig(PAGE_SIZE)) {
            DnDEntityMinPagingSource(
                repository = browseRepository,
                pageSize = PAGE_SIZE,
                entityType = type,
                query = query
            )
        }.flow


    companion object {
        private const val PAGE_SIZE = 20
    }
}