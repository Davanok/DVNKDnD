package com.davanok.dvnkdnd.domain.entities.dndEntities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.enums.dndEnums.AreaTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import com.davanok.dvnkdnd.domain.enums.dndEnums.MagicSchools
import com.davanok.dvnkdnd.domain.enums.dndEnums.SpellComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Spell(
    val school: MagicSchools,
    val level: Int,
    @SerialName("casting_time")
    val castingTime: TimeUnit,
    @SerialName("casting_time_other")
    val castingTimeOther: String?,
    val components: Set<SpellComponents>,
    val ritual: Boolean,
    @SerialName("material_component")
    val materialComponent: String?,
    val duration: String,
    val concentration: Boolean
)

@Immutable
@Serializable
data class FullSpell(
    val spell: Spell,

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
    val id: Uuid = Uuid.random(),
    @SerialName("damage_type")
    val damageType: DamageTypes,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,
    @SerialName("gives_state")
    val givesState: Uuid?,

    @SerialName("level_modifiers")
    val levelModifiers: List<SpellAttackLevelModifierInfo>,
    val save: SpellAttackSaveInfo?
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