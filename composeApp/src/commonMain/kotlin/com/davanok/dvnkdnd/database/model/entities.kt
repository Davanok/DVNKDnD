package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toAbilityLink
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toAbilityRegain
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toArmorInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toFeatInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toProficiency
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toRaceInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellAreaInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellAttackLevelModifierInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellAttackSaveInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellSlots
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toWeaponDamageInfo
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.DnDItemProperty
import com.davanok.dvnkdnd.database.entities.items.ItemPropertyLink
import com.davanok.dvnkdnd.database.entities.items.Weapon
import com.davanok.dvnkdnd.database.entities.items.WeaponDamage
import kotlin.uuid.Uuid


fun DnDBaseEntity.toDnDEntityMin() = DnDEntityMin(
    id = id,
    type = type,
    name = name,
    source = source
)

data class EntityWithSub(
    @Embedded val entity: DnDBaseEntity,
    @Relation(parentColumn = "id", entityColumn = "parent_id")
    val subEntities: List<DnDBaseEntity>,
) {
    fun toEntityWithSubEntities() = DnDEntityWithSubEntities(
        id = entity.id,
        type = entity.type,
        name = entity.name,
        source = entity.source,
        subEntities = subEntities.fastMap { it.toDnDEntityMin() }
    )
}
data class DbClassWithSpells(
    @Embedded
    val cls: DnDClass,
    @Relation(
        ClassSpell::class,
        parentColumn = "id",
        entityColumn = "class_id",
        projection = ["spell_id"]
    )
    val spells: List<Uuid>,
    @Relation(
        parentColumn = "id",
        entityColumn = "class_id"
    )
    val slots: List<ClassSpellSlots>
) {
    fun toClassWithSpells() = ClassWithSpells(
        primaryStats = cls.primaryStats,
        hitDice = cls.hitDice,
        spells = spells,
        slots = slots.fastMap { it.toSpellSlots() }
    )
}
data class DbFullSpellAttack(
    @Embedded
    val attack: SpellAttack,
    @Relation(
        parentColumn = "id",
        entityColumn = "attack_id"
    )
    val modifiers: List<SpellAttackLevelModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val save: SpellAttackSave?
) {
    fun toFullSpellAttack() = FullSpellAttack(
        id = attack.id,
        damageType = attack.damageType,
        diceCount = attack.diceCount,
        dice = attack.dice,
        modifier = attack.modifier,
        modifiers = modifiers.fastMap { it.toSpellAttackLevelModifierInfo() },
        save = save?.toSpellAttackSaveInfo()
    )
}
data class DbFullSpell(
    @Embedded
    val spell: DnDSpell,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val area: SpellArea?,
    @Relation(
        SpellAttack::class,
        parentColumn = "id",
        entityColumn = "spell_id"
    )
    val attacks: List<DbFullSpellAttack>
) {
    fun toFullSpell() = FullSpell(
        school = spell.school,
        level = spell.level,
        castingTime = spell.castingTime,
        components = spell.components,
        ritual = spell.ritual,
        materialComponent = spell.materialComponent,
        duration = spell.duration,
        concentration = spell.concentration,
        area = area?.toSpellAreaInfo(),
        attacks = attacks.fastMap { it.toFullSpellAttack() }
    )
}
data class DbJoinItemProperty(
    @Embedded
    val link: ItemPropertyLink,
    @Relation(
        parentColumn = "property_id",
        entityColumn = "id"
    )
    val property: DnDItemProperty
) {
    fun toJoinItemProperty() = JoinItemProperty(
        itemId = link.itemId,
        propertyId = link.propertyId,
        property = property.toItemProperty()
    )
}
data class DbFullWeapon(
    @Embedded
    val weapon: Weapon,
    @Relation(
        parentColumn = "id",
        entityColumn = "weapon_id"
    )
    val damages: List<WeaponDamage>
) {
    fun toFullWeapon() = FullWeapon(
        atkBonus = weapon.atkBonus,
        damages = damages.fastMap { it.toWeaponDamageInfo() }
    )
}
data class DbFullItem(
    @Embedded
    val item: DnDItem,
    @Relation(
        ItemPropertyLink::class,
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val properties: List<DbJoinItemProperty>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val armor: Armor?,
    @Relation(
        Weapon::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val weapon: DbFullWeapon?
) {
    fun toFullItem() = FullItem(
        cost = item.cost,
        weight = item.weight,
        properties = properties.fastMap { it.toJoinItemProperty() },
        armor = armor?.toArmorInfo(),
        weapon = weapon?.toFullWeapon()
    )
}
data class DbJoinProficiency(
    @Embedded val link: EntityProficiency,
    @Relation(
        parentColumn = "proficiency_id",
        entityColumn = "id"
    )
    val proficiency: DnDProficiency
) {
    fun toJoinProficiency() = JoinProficiency(
        level = link.level,
        proficiency = proficiency.toProficiency()
    )
}
data class DbAbilityInfo(
    @Embedded val ability: DnDAbility,
    @Relation(
        parentColumn = "id",
        entityColumn = "ability_id"
    )
    val regains: List<DnDAbilityRegain>
) {
    fun toAbilityInfo() = AbilityInfo(
        usageLimitByLevel = ability.usageLimitByLevel,
        regains = regains.fastMap { it.toAbilityRegain() }
    )
}
data class DbModifiersGroups(
    @Embedded val group: EntityModifiersGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val modifiers: List<EntityModifier>
) {
    fun toDnDModifiersGroup() = DnDModifiersGroup(
        id = group.id,
        target = group.target,
        operation = group.operation,
        valueSource = group.valueSource,
        name = group.name,
        description = group.description,
        selectionLimit = group.selectionLimit,
        priority = group.priority,
        clampMax = group.clampMax,
        clampMin = group.clampMin,
        minBaseValue = group.minBaseValue,
        maxBaseValue = group.maxBaseValue,
        modifiers = modifiers.fastMap {
            DnDModifier(
                id = it.id,
                selectable = it.selectable,
                value = it.value,
                target = it.target
            )
        }
    )
}
data class DbFullEntity(
    @Embedded
    val base: DnDBaseEntity,

    @Relation(EntityModifiersGroup::class, parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<DbModifiersGroups>,

    @Relation(EntityProficiency::class, parentColumn = "id", entityColumn = "entity_id")
    val proficiencies: List<DbJoinProficiency>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val abilities: List<EntityAbility>,

    @Relation(
        entity = DnDClass::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val cls: DbClassWithSpells?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val race: DnDRace?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val background: DnDBackground?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val feat: DnDFeat?,
    @Relation(
        DnDAbility::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val ability: DbAbilityInfo?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val proficiency: DnDProficiency?,
    @Relation(
        DnDSpell::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val spell: DbFullSpell?,
    @Relation(
        DnDItem::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val item: DbFullItem?
) {
    fun toDnDFullEntity(): DnDFullEntity = DnDFullEntity(
        id = base.id,
        parentId = base.parentId,
        userId = base.userId,
        type = base.type,
        name = base.name,
        description = base.description,
        source = base.source,
        modifiersGroups = modifiers.fastMap { it.toDnDModifiersGroup() },
        proficiencies = proficiencies.fastMap { it.toJoinProficiency() },
        abilities = abilities.fastMap { it.toAbilityLink() },
        cls = cls?.toClassWithSpells(),
        race = race?.toRaceInfo(),
        background = null,
        feat = feat?.toFeatInfo(),
        ability = ability?.toAbilityInfo(),
        spell = spell?.toFullSpell(),
        item = item?.toFullItem(),
        companionEntities = emptyList()
    )
}


