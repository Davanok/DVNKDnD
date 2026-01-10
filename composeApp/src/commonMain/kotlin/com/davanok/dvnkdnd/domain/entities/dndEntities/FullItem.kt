package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageConditionType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemEffectScope
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemPropertyType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemsRarity
import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class Item(
    val cost: Int?, // in copper pieces
    val weight: Int?, // in grams
    val equippable: Boolean,
    val rarity: ItemsRarity
)

@Serializable
data class FullItem(
    val item: Item,

    val effects: List<ItemEffect>,
    val activations: List<FullItemActivation>,

    val properties: List<ItemProperty>,
    val armor: ArmorInfo?,
    val weapon: FullWeapon?,
) {
    fun requiresAttunement(): Boolean =
        effects.any { it.scope == ItemEffectScope.ATTUNED }
                || activations.any { it.requiresAttunement }
}

@Serializable
data class ItemEffect(
    val id: Uuid,
    val scope: ItemEffectScope,
    @SerialName("gives_state")
    val givesState: Uuid
)

@Serializable
data class FullItemActivation(
    val id: Uuid,
    val name: String,
    @SerialName("requires_attunement")
    val requiresAttunement: Boolean,
    @SerialName("gives_state")
    val givesState: Uuid?,
    val count: Int?,

    @SerialName("casts_spell")
    val castsSpell: ItemActivationCastsSpell?,
    val regains: List<ItemActivationRegain>
)

@Serializable
data class ItemActivationCastsSpell(
    @SerialName("spell_id")
    val spellId: Uuid,
    val level: Int
)

@Serializable
data class ItemActivationRegain(
    val id: Uuid,
    @SerialName("regains_count")
    val regainsCount: Int?,
    @SerialName("time_unit")
    val timeUnit: TimeUnit,
    @SerialName("time_unit_count")
    val timeUnitCount: Int
)

@Serializable
data class ArmorInfo(
    @SerialName("armor_class")
    val armorClass: Int,
    @SerialName("dex_max_modifier")
    val dexMaxModifier: Int?,
    @SerialName("required_strength")
    val requiredStrength: Int?,
    @SerialName("stealth_disadvantage")
    val stealthDisadvantage: Boolean,
)
@Serializable
data class ItemProperty(
    val id: Uuid,
    @SerialName("user_id")
    val userId: Uuid?,
    val type: ItemPropertyType,
    val name: String,
    val description: String,
)
@Serializable
data class FullWeapon(
    @SerialName("atk_bonus")
    val atkBonus: Int,

    val damages: List<WeaponDamageInfo>,
)
@Serializable
data class WeaponDamageInfo(
    val id: Uuid = Uuid.random(),
    @SerialName("damage_type")
    val damageType: DamageTypes,
    @SerialName("dice_count")
    val diceCount: Int,
    val dice: Dices,
    val modifier: Int,

    val condition: DamageCondition?
)
@Serializable
data class DamageCondition(
    val type: DamageConditionType,
    val target: String?
)