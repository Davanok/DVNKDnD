package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbRace

@Dao
interface EntityInfoDao: AbilityDao, ClassDao, ItemDao, SpellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: DbRace)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackground(background: DbBackground)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeat(feat: DbFeat)
}