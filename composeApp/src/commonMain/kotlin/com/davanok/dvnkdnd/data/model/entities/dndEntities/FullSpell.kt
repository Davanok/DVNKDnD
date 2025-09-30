package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.AreaTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DamageTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.MagicSchools
import com.davanok.dvnkdnd.data.model.dndEnums.SpellComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class FullSpell(
    val school: MagicSchools,
    val level: Int?,
    @SerialName("casting_time")
    val castingTime: String,
    val components: List<SpellComponents>,
    val ritual: Boolean,
    @SerialName("material_component")
    val materialComponent: String?,
    val duration: String,
    val concentration: Boolean,

    val area: SpellAreaInfo?,
    val attacks: List<FullSpellAttack>,
)

@Serializable
data class SpellAreaInfo(
    val range: Int,
    val type: AreaTypes,
    val width: Int,
    val height: Int
)

@Serializable
data class FullSpellAttack(
    val id: Uuid,
    @SerialName("damage_type")
    val damageType: DamageTypes,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,

    val modifiers: List<SpellAttackLevelModifierInfo>,
    val save: SpellAttackSaveInfo?,
)

@Serializable
data class SpellAttackSaveInfo(
    @SerialName("saving_throw")
    val savingThrow: Attributes,
    @SerialName("half_on_success")
    val halfOnSuccess: Boolean,
)

@Serializable
data class SpellAttackLevelModifierInfo(
    val id: Uuid = Uuid.random(),
    val level: Int,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
)