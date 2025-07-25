package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.DamageTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Entity(
    tableName = "weapons",
    foreignKeys = [ForeignKey(DnDItem::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)]
)
data class Weapon(
    @PrimaryKey val id: Uuid,
    val atkBonus: Int
)
@Serializable
@Entity(
    tableName = "weapon_damages",
    foreignKeys = [ForeignKey(Weapon::class, ["id"], ["weapon_id"], onDelete = ForeignKey.CASCADE)]
)
data class WeaponDamage(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("weapon_id", index = true) val weaponId: Uuid,
    val damageType: DamageTypes,
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int
)