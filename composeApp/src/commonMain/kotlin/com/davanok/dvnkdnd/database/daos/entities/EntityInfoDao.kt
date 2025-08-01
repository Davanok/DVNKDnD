package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace

@Dao
interface EntityInfoDao: ClassDao, ItemDao, SpellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: DnDRace)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackground(background: DnDBackground)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeat(feat: DnDFeat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbility(ability: DnDAbility)
}