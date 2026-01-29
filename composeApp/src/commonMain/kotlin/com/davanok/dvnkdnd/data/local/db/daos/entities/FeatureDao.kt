package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeature
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeatureRegain
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullFeature
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbFeature
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbFeatureRegain
import kotlin.uuid.Uuid

@Dao
interface FeatureDao {
    @Insert
    suspend fun insertFeature(ability: DbFeature)

    @Insert
    suspend fun insertFeatureRegains(regains: List<DbFeatureRegain>)

    @Transaction
    suspend fun insertFullFeature(entityId: Uuid, feature: FullFeature) {
        insertFeature(feature.toDbFeature(entityId))
        insertFeatureRegains(
            feature.regains.map { it.toDbFeatureRegain(entityId) }
        )
    }
}