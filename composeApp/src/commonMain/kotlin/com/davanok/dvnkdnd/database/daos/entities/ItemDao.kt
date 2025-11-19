package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.database.entities.items.DbArmor
import com.davanok.dvnkdnd.database.entities.items.DbItem
import com.davanok.dvnkdnd.database.entities.items.DbItemProperty
import com.davanok.dvnkdnd.database.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.DbWeapon
import com.davanok.dvnkdnd.database.entities.items.DbWeaponDamage
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbArmor
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbItem
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbItemProperty
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbItemPropertyLink
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbWeapon
import com.davanok.dvnkdnd.database.model.adapters.entities.toDbWeaponDamage
import kotlin.uuid.Uuid

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DbItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemProperties(properties: List<DbItemProperty>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPropertyLinks(link: List<DbItemPropertyLink>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArmor(armor: DbArmor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapon(weapon: DbWeapon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeaponDamages(damages: List<DbWeaponDamage>)

    @Transaction
    suspend fun insertFullItem(entityId: Uuid, item: FullItem) {
        insertItem(item.toDbItem(entityId))
        insertItemProperties(item.properties.fastMap { it.property.toDbItemProperty() })
        insertItemPropertyLinks(item.properties.fastMap { it.toDbItemPropertyLink() })
        item.armor?.let { insertArmor(it.toDbArmor(entityId)) }
        item.weapon?.let { insertFullWeapon(entityId, it) }
    }

    @Transaction
    suspend fun insertFullWeapon(entityId: Uuid, weapon: FullWeapon) {
        insertWeapon(weapon.toDbWeapon(entityId))
        insertWeaponDamages(weapon.damages.fastMap { it.toDbWeaponDamage(entityId) })
    }
}