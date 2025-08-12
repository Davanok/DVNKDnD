package com.davanok.dvnkdnd.data.model.entities.dndEntities

import androidx.compose.ui.unit.IntSize
import com.davanok.dvnkdnd.data.model.dndEnums.AreaTypes
import com.davanok.dvnkdnd.data.model.dndEnums.DamageTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.MagicSchools
import com.davanok.dvnkdnd.data.model.dndEnums.SpellComponents
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
) {
    fun toSpell(entityId: Uuid) = DnDSpell(
        entityId,
        school,
        level,
        castingTime,
        components,
        ritual,
        materialComponent,
        duration,
        concentration
    )
}
@Serializable
data class SpellAreaInfo(
    val range: Int,
    val type: AreaTypes,
    val width: Int,
    val height: Int
)
fun SpellArea.toSpellAreaInfo() = SpellAreaInfo(
    range = range,
    type = type,
    width = width,
    height = height
)
fun SpellAreaInfo.toSpellArea(entityId: Uuid) = SpellArea(
    id = entityId,
    range = range,
    type = type,
    width = width,
    height = height
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
) {
    fun toAttack(spellId: Uuid) = SpellAttack(
        id, spellId, damageType, diceCount, dice, modifier
    )
}
@Serializable
data class SpellAttackSaveInfo(
    @SerialName("saving_throw")
    val savingThrow: Stats,
    @SerialName("half_on_success")
    val halfOnSuccess: Boolean,
)
fun SpellAttackSave.toSpellAttackSaveInfo() = SpellAttackSaveInfo(
    savingThrow = savingThrow,
    halfOnSuccess = halfOnSuccess
)
fun SpellAttackSaveInfo.toSpellAttackSave(attackId: Uuid) = SpellAttackSave(
    id = attackId,
    savingThrow = savingThrow,
    halfOnSuccess = halfOnSuccess
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
fun SpellAttackLevelModifier.toSpellAttackLevelModifierInfo() = SpellAttackLevelModifierInfo(
    id = id,
    level = level,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)
fun SpellAttackLevelModifierInfo.toSpellAttackLevelModifier(attackId: Uuid) = SpellAttackLevelModifier(
    id = id,
    attackId = attackId,
    level = level,
    diceCount = diceCount,
    dice = dice,
    modifier = modifier
)