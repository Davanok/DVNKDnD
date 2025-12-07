package com.davanok.dvnkdnd.data.local.db.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import kotlin.uuid.Uuid


@Entity(
    tableName = "weapons",
    foreignKeys = [ForeignKey(DbItem::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class DbWeapon(
    @PrimaryKey val id: Uuid,
    val atkBonus: Int
)
@Entity(
    tableName = "weapon_damages",
    foreignKeys = [ForeignKey(DbWeapon::class, ["id"], ["weapon_id"], onDelete = ForeignKey.CASCADE)]
)
data class DbWeaponDamage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("weapon_id", index = true) val weaponId: Uuid,
    @ColumnInfo("damage_type") val damageType: DamageTypes,
    @ColumnInfo("dice_count") val diceCount: Int,
    val dice: Dices,
    val modifier: Int
)