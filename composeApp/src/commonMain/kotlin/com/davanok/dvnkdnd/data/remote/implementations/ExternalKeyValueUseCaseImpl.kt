package com.davanok.dvnkdnd.data.remote.implementations

import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import com.davanok.dvnkdnd.domain.repositories.remote.ExternalKeyValueRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class ExternalKeyValueRepositoryImpl(
    private val browseRepository: BrowseRepository
) : ExternalKeyValueRepository {
    override suspend fun getRequiredEntities(): Result<List<Uuid>> =
        browseRepository.getPropertyValue("primary_base_entities").mapCatching {
            Json.decodeFromString(it)
        }
}