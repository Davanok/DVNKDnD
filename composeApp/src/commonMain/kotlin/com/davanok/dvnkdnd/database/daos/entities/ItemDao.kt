package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DnDItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemProperties(properties: List<ItemProperty>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPropertyLinks(link: List<ItemPropertyLink>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArmor(armor: Armor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapon(weapon: Weapon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeaponDamages(damages: List<WeaponDamage>)

    @Transaction
    suspend fun insertFullItem(item: FullItem) {
        insertItem(item.toDnDItem())
        insertItemProperties(item.properties.map { it.property })
        insertItemPropertyLinks(item.properties.map { it.toItemPropertyLink() })
        item.armor?.let { insertArmor(it) }
        item.weapon?.let { insertFullWeapon(it) }
    }

    @Transaction
    suspend fun insertFullWeapon(weapon: FullWeapon) {
        insertWeapon(weapon.toWeapon())
        insertWeaponDamages(weapon.damages)
    }
}