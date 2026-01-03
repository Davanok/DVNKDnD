package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.items.DbArmor
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItem
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivation
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationCastsSpell
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemActivationRegain
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemEffect
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemProperty
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemPropertyLink
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeapon
import com.davanok.dvnkdnd.data.local.db.entities.items.DbWeaponDamage
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbArmor
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbItem
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbItemActivation
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbItemActivationCastsSpell
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbItemActivationRegain
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbItemEffect
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbItemProperty
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbWeapon
import com.davanok.dvnkdnd.data.local.mappers.entities.toDbWeaponDamage
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemProperty
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEffects(effects: List<DbItemEffect>)

    @Transaction
    suspend fun insertItemProperties(itemId: Uuid, properties: List<ItemProperty>) {
        insertItemProperties(properties.map { it.toDbItemProperty() })
        insertItemPropertyLinks(properties.map { DbItemPropertyLink(itemId = itemId, propertyId = it.id) })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemActivation(activation: DbItemActivation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemActivationCastsSpell(castsSpell: DbItemActivationCastsSpell)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemActivationRegains(regains: List<DbItemActivationRegain>)

    @Transaction
    suspend fun insertFullItemActivation(itemId: Uuid, activation: FullItemActivation) {
        insertItemActivation(activation.toDbItemActivation(itemId))
        activation.castsSpell
            ?.toDbItemActivationCastsSpell(activation.id)
            ?.let { insertItemActivationCastsSpell(it) }
        activation.regains
            .map { it.toDbItemActivationRegain(activation.id) }
            .let { insertItemActivationRegains(it) }
    }

    @Transaction
    suspend fun insertFullItem(entityId: Uuid, item: FullItem) {
        insertItem(item.item.toDbItem(entityId))

        item.effects
            .map { it.toDbItemEffect(entityId) }
            .let { insertItemEffects(it) }
        item.activations
            .forEach { insertFullItemActivation(entityId, it) }

        insertItemProperties(entityId, item.properties)

        item.armor?.let { insertArmor(it.toDbArmor(entityId)) }
        item.weapon?.let { insertFullWeapon(entityId, it) }
    }

    @Transaction
    suspend fun insertFullWeapon(entityId: Uuid, weapon: FullWeapon) {
        insertWeapon(weapon.toDbWeapon(entityId))
        insertWeaponDamages(weapon.damages.map { it.toDbWeaponDamage(entityId) })
    }
}