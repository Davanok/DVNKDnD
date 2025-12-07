package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace

@Dao
interface EntityInfoDao: AbilityDao, ClassDao, ItemDao, SpellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: DbRace)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackground(background: DbBackground)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeat(feat: DbFeat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(background: DbState)
}