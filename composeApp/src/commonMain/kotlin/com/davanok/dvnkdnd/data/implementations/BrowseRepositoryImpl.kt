package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage

class BrowseRepositoryImpl(
    private val postgrest: Postgrest,
    private val storage: Storage
): BrowseRepository {
    override suspend fun loadEntityFullInfo(
        entityType: DnDEntityTypes,
        entityId: Long
    ): DnDEntityFullInfo {
        return postgrest.from(entityType.name).select().decodeSingle()
    }

    override suspend fun loadEntities(entityType: DnDEntityTypes): List<DnDEntityWithSubEntities> {
        val result = postgrest.from("base_entities").select (
            columns = Columns.raw("*, subEntities:base_entities(*)")
        ) {
            filter { DnDEntityWithSubEntities::type eq entityType.name }
        }.decodeList<DnDEntityWithSubEntities>()
        return result
    }
}