package com.davanok.dvnkdnd.database.daos.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toArmor
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toWeaponDamage
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlin.uuid.Uuid

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DnDItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemProperties(properties: List<DnDItemProperty>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPropertyLinks(link: List<ItemPropertyLink>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArmor(armor: Armor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapon(weapon: Weapon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeaponDamages(damages: List<WeaponDamage>)

    @Transaction
    suspend fun insertFullItem(entityId: Uuid, item: FullItem) {
        insertItem(item.toDnDItem(entityId))
        insertItemProperties(item.properties.map { it.property.toDnDItemProperty() })
        insertItemPropertyLinks(item.properties.map { it.toItemPropertyLink() })
        item.armor?.let { insertArmor(it.toArmor(entityId)) }
        item.weapon?.let { insertFullWeapon(entityId, it) }
    }

    @Transaction
    suspend fun insertFullWeapon(entityId: Uuid, weapon: FullWeapon) {
        insertWeapon(weapon.toWeapon(entityId))
        insertWeaponDamages(weapon.damages.fastMap { it.toWeaponDamage(entityId) })
    }
}