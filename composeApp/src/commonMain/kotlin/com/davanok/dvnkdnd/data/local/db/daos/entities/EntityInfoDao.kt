package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbBackground

@Dao
interface EntityInfoDao: FeatureDao, ClassDao, RaceDao, ItemDao, SpellDao, StateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackground(background: DbBackground)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeat(feat: DbFeat)
}