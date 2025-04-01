package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiencies
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill

@Dao
interface ClassesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cls: DnDClass)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: EntitySkill)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingThrow(savingThrow: EntitySavingThrow)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiency(proficiency: EntityProficiencies)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbility(ability: EntityAbility)
}