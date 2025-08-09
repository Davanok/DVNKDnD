package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.ExternalKeyValueRepository
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class ExternalKeyValueRepositoryImpl(
    private val browseRepository: BrowseRepository
): ExternalKeyValueRepository {
    override suspend fun getRequiredEntities(): Result<List<Uuid>> =
        browseRepository.getPropertyValue("primary_base_entities").mapCatching {
            Json.decodeFromString(it)
        }
}