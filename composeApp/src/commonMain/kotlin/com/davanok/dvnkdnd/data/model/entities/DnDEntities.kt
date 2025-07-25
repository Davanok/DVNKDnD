package com.davanok.dvnkdnd.data.model.entities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dnd_enums.DamageTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.Dices
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.MagicSchools
import com.davanok.dvnkdnd.data.model.dnd_enums.SpellComponents
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlot
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Immutable
@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)

@Immutable
@Serializable
data class DnDEntityWithSubEntities(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    @SerialName("sub_entities")
    val subEntities: List<DnDEntityMin>,
) {
    fun toDnDEntityMin() = DnDEntityMin(id, type, name, source)
}

@Immutable
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val selectionLimit: Int?,
    val modifiers: List<DnDModifierBonus>,
)

@Immutable
data class DnDEntityWithSkills(
    val entity: DnDEntityMin,
    val selectionLimit: Int?,
    val skills: List<DnDSkill>,
)

@Immutable
@Serializable
data class DnDFullEntity(
    val id: Uuid = Uuid.random(),
    @SerialName("parent_id")
    val parentId: Uuid?,
    @SerialName("user_id")
    val userId: Uuid?,
    val type: DnDEntityTypes,
    val shared: Boolean = false,
    val name: String,
    val description: String,
    val source: String,

    @SerialName("modifier_bonuses")
    val modifierBonuses: List<DnDModifierBonus>,
    val skills: List<DnDSkill>,
    @SerialName("saving_throws")
    val savingThrows: List<EntitySavingThrow>,

    val proficiencies: List<EntityProficiency>,
    val abilities: List<EntityAbility>,

    @SerialName("selection_limits")
    val selectionLimits: EntitySelectionLimits?,

    val cls: ClassWithSpells?,
    val race: DnDRace?,
    val background: DnDBackground?,
    val feat: DnDFeat?,
    val ability: DnDAbility?,
    val proficiency: DnDProficiency?,
    val spell: FullSpell?,
    val item: FullItem?,

    @SerialName("companion_entities")
    val companionEntities: List<DnDFullEntity> = emptyList(),
) {
    fun toBaseEntity() = DnDBaseEntity(
        id = id,
        parentId = parentId,
        type = type,
        shared = shared,
        name = name,
        description = description,
        source = source
    )
    fun toDnDEntityMin() = DnDEntityMin(
        id = id,
        type = type,
        name = name,
        source = source
    )

    fun getSubEntitiesIds() =
        abilities.fastMap { it.abilityId } +
                proficiencies.fastMap { it.proficiencyId } +
                cls?.spells?.fastMap { it.spellId }.orEmpty()
}

@Serializable
data class ClassWithSpells(
    val id: Uuid,
    @SerialName("main_stat")
    val mainStat: Stats,
    @SerialName("hit_dice")
    val hitDice: Dices,

    val spells: List<ClassSpell>,
    val slots: List<ClassSpellSlot>,
) {
    val cls get() = DnDClass(id, mainStat, hitDice)
}

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
    val spell
        get() = DnDSpell(
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
    val attack
        get() = SpellAttack(
            id, spellId, damageType, diceCount, dice, modifier
        )
}

@Serializable
data class FullItem(
    val id: Uuid,
    val cost: Int?, // in copper pieces
    val weight: Int?,

    val properties: List<JoinItemProperty>,
    val armor: Armor?,
    val weapon: FullWeapon?,
) {
    val item get() = DnDItem(id, cost, weight)
}

@Serializable
data class JoinItemProperty(
    @SerialName("item_id")
    val itemId: Uuid,
    @SerialName("property_id")
    val propertyId: Uuid,

    val property: ItemProperty,
) {
    val link get() = ItemPropertyLink(itemId, propertyId)
}

@Serializable
data class FullWeapon(
    val id: Uuid,
    @SerialName("atk_bonus")
    val atkBonus: Int,

    val damages: List<WeaponDamage>,
) {
    val weapon get() = Weapon(id, atkBonus)
}