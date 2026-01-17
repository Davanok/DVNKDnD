package com.davanok.dvnkdnd.core

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository

class DnDEntityMinPagingSource(
    private val repository: BrowseRepository,
    private val pageSize: Int,
    private val entityType: DnDEntityTypes,
    private val query: String?
) : PagingSource<Int, DnDEntityWithSubEntities>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DnDEntityWithSubEntities> {
        val nextPageNumber = params.key ?: 1
        return repository.loadEntitiesWithSubPaged(
            entityType = entityType,
            page = nextPageNumber,
            pageSize = pageSize,
            searchQuery = query
        ).fold(
            onSuccess = { 
                LoadResult.Page(
                    data = it.items,
                    prevKey = if (it.hasPrevious) nextPageNumber - 1 else null,
                    nextKey = if (it.hasNext) nextPageNumber + 1 else null
                )
                        },
            onFailure = { LoadResult.Error(it) }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, DnDEntityWithSubEntities>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}