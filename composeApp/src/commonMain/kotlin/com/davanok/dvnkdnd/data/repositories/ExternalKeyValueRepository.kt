package com.davanok.dvnkdnd.data.repositories

import kotlin.uuid.Uuid

interface ExternalKeyValueRepository {
    suspend fun getRequiredEntities(): Result<List<Uuid>>
}