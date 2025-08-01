package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency

@Dao
interface EntityAttributesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DnDBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencies(proficiencies: List<DnDProficiency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectionLimits(selectionLimit: EntitySelectionLimits)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifierBonuses(modifiers: List<EntityModifierBonus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<EntitySkill>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingThrows(throws: List<EntitySavingThrow>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProficiencyLinks(proficiency: List<EntityProficiency>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityLinks(ability: List<EntityAbility>)
}