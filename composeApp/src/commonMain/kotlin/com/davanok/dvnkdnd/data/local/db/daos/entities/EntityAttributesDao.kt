package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbEntityModifier
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbEntityModifiersGroup
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