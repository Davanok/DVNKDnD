package com.davanok.dvnkdnd.domain.repositories.remote

import kotlin.uuid.Uuid

interface ExternalKeyValueRepository {
    suspend fun getRequiredEntities(): Result<List<Uuid>>
}