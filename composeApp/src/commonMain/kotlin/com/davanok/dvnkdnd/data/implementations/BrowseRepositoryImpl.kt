@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BrowseRepositoryImpl(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : BrowseRepository {
    override suspend fun loadEntityFullInfo(entityWebId: Uuid): DnDEntityFullInfo {
        return postgrest.from("base_entities").select{
            filter {  }
        }.decodeSingle()
    }

    override suspend fun loadEntitiesFullInfo(entityWebId: List<Uuid>): List<DnDEntityFullInfo> {
        return postgrest.from("base_entities").select{
            filter {  }
        }.decodeList()
    }

    override suspend fun loadEntities(entityType: DnDEntityTypes): List<DnDEntityWithSubEntities> {
        val result = postgrest.from("base_entities").select(
            columns = Columns.raw("*, subEntities:base_entities(*)")
        ) {
            filter { DnDEntityWithSubEntities::type eq entityType.name }
        }.decodeList<DnDEntityWithSubEntities>()
        return result
    }

    override suspend fun getValue(key: String): String {
        val result = postgrest.from("key_value").select(Columns.list("value")) {
            filter { eq("key", key) }
        }.decodeAs<String>()
        return result
    }
}