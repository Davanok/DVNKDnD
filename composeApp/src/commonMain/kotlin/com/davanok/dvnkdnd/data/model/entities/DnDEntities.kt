package com.davanok.dvnkdnd.data.model.entities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.database.entities.DnDProficiency
import com.davanok.dvnkdnd.database.entities.DnDAbility
import com.davanok.dvnkdnd.database.entities.Spell
import com.davanok.dvnkdnd.database.entities.SpellArea
import com.davanok.dvnkdnd.database.entities.SpellAttack
import com.davanok.dvnkdnd.database.entities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import com.davanok.dvnkdnd.database.entities.dndEntities.RaceSize
import com.davanok.dvnkdnd.database.entities.dndEntities.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)
@Immutable
@Serializable
data class DnDEntityWithSubEntities (
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    @SerialName("sub_entities")
    val subEntities: List<DnDEntityMin>
) {
    fun asDnDEntityMin() = DnDEntityMin(id, type, name, source)
}
@Immutable
@Serializable
data class DnDFullEntity(
    val base: DnDBaseEntity,

    val modifiers: List<EntityModifier>,
    val skills: List<EntitySkill>,
    @SerialName("saving_throws")
    val savingThrows: List<EntitySavingThrow>,

    val proficiencies: List<JoinProficiency>,
    val abilities: List<JoinAbility>,

    @SerialName("selection_limits")
    val selectionLimits: EntitySelectionLimits?,

    val cls: ClassWithSpells? = null,
    val race: RaceWithSizes? = null,
    val background: DnDBackground? = null,
    val feat: DnDFeat? = null,
    val ability: DnDAbility? = null,
    val proficiency: DnDProficiency? = null,
    val spell: FullSpell? = null,
    val item: FullItem? = null,

    @SerialName("companion_entities")
    val companionEntities: List<DnDFullEntity> = emptyList()
)
@Serializable
data class JoinProficiency(
    val link: EntityProficiency,
    val proficiency: ProficiencyWithInfo
)
@Serializable
data class JoinAbility(
    val link: EntityAbility,
    val ability: AbilityWithInfo
)
@Serializable
data class AbilityWithInfo(
    val ability: DnDAbility,
    val base: DnDBaseEntity
)
@Serializable
data class ProficiencyWithInfo(
    val proficiency: DnDProficiency,
    val base: DnDBaseEntity
)
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val selectionLimit: Int? = null,
    val modifiers: List<DnDModifier> = emptyList()
)
@Serializable
data class ClassWithSpells(
    val cls: DnDClass,
    val spells: List<ClassSpell>,
    val slots: List<ClassSpellSlots>
)
@Serializable
data class RaceWithSizes(
    val race: DnDRace,
    val sizes: List<RaceSize>
)
@Serializable
data class FullSpell(
    val spell: Spell,
    val area: SpellArea,
    val attacks: List<FullSpellAttack>
)
@Serializable
data class FullSpellAttack(
    val attack: SpellAttack,
    val modifiers: List<SpellAttackLevelModifier>,
    val save: SpellAttackSave?
)
@Serializable
data class FullItem(
    val item: DnDItem,
    val properties: List<JoinProperty>,
    val armor: Armor? = null,
    val weapon: FullWeapon? = null,
)
@Serializable
data class JoinProperty(
    val link: ItemPropertyLink,
    val property: ItemProperty
)
@Serializable
data class FullWeapon(
    val weapon: Weapon,
    val damages: List<WeaponDamage>
)