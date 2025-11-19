package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbEntityModifier
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbEntityModifiersGroup
import kotlin.uuid.Uuid

@Dao
interface EntityAttributesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DbBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencies(proficiencies: List<DbProficiency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntityModifiersGroup(modifiersGroup: DbEntityModifiersGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiers(modifiers: List<DbEntityModifier>)

    @Transaction
    suspend fun insertModifiersGroup(entityId: Uuid, modifiersGroup: ModifiersGroup) {
        val groupId = modifiersGroup.id

        insertEntityModifiersGroup(modifiersGroup.toDbEntityModifiersGroup(entityId))
        insertModifiers(modifiersGroup.modifiers.map { it.toDbEntityModifier(groupId) })
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProficiencyLinks(proficiency: List<DbEntityProficiency>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityLinks(ability: List<DbEntityAbility>)
}