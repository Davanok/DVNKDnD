package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityFeature
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityValueModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbEntityDamageModifier
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbEntityRollModifier
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbEntityValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDDamageModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDRollModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import kotlin.uuid.Uuid

@Dao
interface EntityAttributesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DbBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntityImages(images: List<DbEntityImage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencies(proficiencies: List<DbProficiency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntityModifiersGroup(modifiersGroup: DbEntityModifiersGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValueModifiers(modifiers: List<DbEntityValueModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRollModifiers(modifiers: List<DbEntityRollModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDamageModifiers(modifiers: List<DbEntityDamageModifier>)

    @Transaction
    suspend fun insertModifiersGroup(entityId: Uuid, modifiersGroup: ModifiersGroup) {
        val groupId = modifiersGroup.id

        insertEntityModifiersGroup(modifiersGroup.toDbEntityModifiersGroup(entityId))

        val valueModifiers = mutableListOf<DbEntityValueModifier>()
        val rollModifiers = mutableListOf<DbEntityRollModifier>()
        val damageModifiers = mutableListOf<DbEntityDamageModifier>()

        modifiersGroup.modifiers.forEach { modifier ->
            when (modifier) {
                is DnDValueModifier -> valueModifiers.add(modifier.toDbEntityValueModifier(groupId))
                is DnDRollModifier -> rollModifiers.add(modifier.toDbEntityRollModifier(groupId))
                is DnDDamageModifier -> damageModifiers.add(modifier.toDbEntityDamageModifier(groupId))
            }
        }

        if (valueModifiers.isNotEmpty()) insertValueModifiers(valueModifiers)
        if (rollModifiers.isNotEmpty()) insertRollModifiers(rollModifiers)
        if (damageModifiers.isNotEmpty()) insertDamageModifiers(damageModifiers)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProficiencyLinks(proficiency: List<DbEntityProficiency>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeaturesLinks(ability: List<DbEntityFeature>)
}