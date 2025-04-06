@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.DamageTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import com.davanok.dvnkdnd.database.entities.Proficiency
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Entity(
    tableName = "weapons",
    foreignKeys = [ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE)]
)
data class Weapon(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val itemId: Uuid,
    val atkBonus: Int
)
@Entity(
    tableName = "weapon_damages",
    foreignKeys = [ForeignKey(Weapon::class, ["id"], ["weaponId"], onDelete = ForeignKey.CASCADE)]
)
data class WeaponDamage(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val weaponId: Uuid,
    val damageType: DamageTypes,
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int
)
@Entity(
    tableName = "weapon_properties",
    foreignKeys = [
        ForeignKey(Weapon::class, ["id"], ["weaponId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class WeaponProperties(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val weaponId: Uuid,
    @ColumnInfo(index = true) val proficiencyId: Uuid
)
