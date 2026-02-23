package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbRaceSpeedValues
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbRace
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbRaceSpeedValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullRace
import kotlin.uuid.Uuid

@Dao
interface RaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: DbRace)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaceSpeedValues(speed: DbRaceSpeedValues)

    @Transaction
    suspend fun insertFullRace(entityId: Uuid, race: FullRace) {
        insertRace(race.toDbRace(entityId))
        insertRaceSpeedValues(race.speedValues.toDbRaceSpeedValues(entityId))
    }
}