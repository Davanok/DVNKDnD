package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.DamageTypes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.MagicSchools
import com.davanok.dvnkdnd.data.model.dndEnums.SpellComponents
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class FullSpell(
    val id: Uuid,
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

    val area: SpellArea?,
    val attacks: List<FullSpellAttack>,
) {
    fun toSpell() = DnDSpell(
        id,
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
data class FullSpellAttack(
    val id: Uuid,
    @SerialName("spell_id")
    val spellId: Uuid,
    @SerialName("damage_type")
    val damageType: DamageTypes,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,

    val modifiers: List<SpellAttackLevelModifier>,
    val save: SpellAttackSave?,
) {
    fun toAttack() = SpellAttack(
        id, spellId, damageType, diceCount, dice, modifier
    )
}
