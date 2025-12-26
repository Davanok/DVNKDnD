package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbStateDuration
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbState
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbStateDuration
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullState
import kotlin.uuid.Uuid

@Dao
interface StateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStateDuration(duration: DbStateDuration)

    @Insert
    suspend fun insertState(state: DbState)

    @Transaction
    suspend fun insertState(entityId: Uuid, state: FullState) {
        insertState(state.toDbState(entityId))
        state.duration?.let {
            insertStateDuration(it.toDbStateDuration(entityId))
        }
    }
}