package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.model.adapters.entities.toEntityModifier
import com.davanok.dvnkdnd.database.model.adapters.entities.toEntityModifiersGroup
import kotlin.uuid.Uuid

@Dao
interface EntityAttributesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DnDBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencies(proficiencies: List<DnDProficiency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntityModifiersGroup(modifiersGroup: EntityModifiersGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiers(modifiers: List<EntityModifier>)

    @Transaction
    suspend fun insertModifiersGroup(entityId: Uuid, modifiersGroup: DnDModifiersGroup) {
        val groupId = modifiersGroup.id

        insertEntityModifiersGroup(modifiersGroup.toEntityModifiersGroup(entityId))
        insertModifiers(modifiersGroup.modifiers.fastMap { it.toEntityModifier(groupId) })
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProficiencyLinks(proficiency: List<EntityProficiency>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAbilityLinks(ability: List<EntityAbility>)
}