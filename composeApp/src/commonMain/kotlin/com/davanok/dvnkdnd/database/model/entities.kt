package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSkills
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullWeapon
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinItemProperty
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toAbilityLink
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toDnDSelectionLimits
import com.davanok.dvnkdnd.data.model.entities.dndEntities.toSpellSlots
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toDnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toDnDSavingThrow
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toDnDSkill
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
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import com.davanok.dvnkdnd.database.entities.items.Armor
import com.davanok.dvnkdnd.database.entities.items.DnDItem
import com.davanok.dvnkdnd.database.entities.items.ItemProperty
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

data class DbEntityWithModifiers(
    @Embedded val entity: DnDBaseEntity,
    @Relation(
        entity = EntitySelectionLimits::class,
        parentColumn = "id",
        entityColumn = "id",
        projection = ["modifiers"]
    )
    val selectionLimit: Int?,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val modifiers: List<EntityModifierBonus>,
) {
    fun toDnDEntityWithModifiers() = DnDEntityWithModifiers(
        entity = entity.toDnDEntityMin(),
        selectionLimit = selectionLimit,
        modifiers = modifiers.fastMap { it.toDnDModifier() }
    )
}
data class DbEntityWithSkills(
    @Embedded val entity: DnDBaseEntity,
    @Relation(
        entity = EntitySelectionLimits::class,
        parentColumn = "id",
        entityColumn = "id",
        projection = ["skills"]
    )
    val selectionLimit: Int?,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val skills: List<EntitySkill>,
) {
    fun toDndEntityWithSkills() = DnDEntityWithSkills(
        entity = entity.toDnDEntityMin(),
        selectionLimit = selectionLimit,
        skills = skills.fastMap { it.toDnDSkill() }
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
        mainStats = cls.mainStats,
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
        spellId = attack.spellId,
        damageType = attack.damageType,
        diceCount = attack.diceCount,
        dice = attack.dice,
        modifier = attack.modifier,
        modifiers = modifiers,
        save = save
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
        id = spell.id,
        school = spell.school,
        level = spell.level,
        castingTime = spell.castingTime,
        components = spell.components,
        ritual = spell.ritual,
        materialComponent = spell.materialComponent,
        duration = spell.duration,
        concentration = spell.concentration,
        area = area,
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
    val property: ItemProperty
) {
    fun toJoinItemProperty() = JoinItemProperty(
        itemId = link.itemId,
        propertyId = link.propertyId,
        property = property
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
        id = weapon.id,
        atkBonus = weapon.atkBonus,
        damages = damages
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
        id = item.id,
        cost = item.cost,
        weight = item.weight,
        properties = properties.fastMap { it.toJoinItemProperty() },
        armor = armor,
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
        proficiency = proficiency
    )
}
data class DbFullEntity(
    @Embedded
    val base: DnDBaseEntity,

    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val modifierBonuses: List<EntityModifierBonus>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val skills: List<EntitySkill>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val savingThrows: List<EntitySavingThrow>,

    @Relation(EntityProficiency::class, parentColumn = "id", entityColumn = "entity_id")
    val proficiencies: List<DbJoinProficiency>,
    @Relation(parentColumn = "id", entityColumn = "entity_id")
    val abilities: List<EntityAbility>,

    @Relation(parentColumn = "id", entityColumn = "id")
    val selectionLimits: EntitySelectionLimits?,

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
        parentColumn = "id",
        entityColumn = "id"
    )
    val ability: DnDAbility?,
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
        modifierBonuses = modifierBonuses.fastMap { it.toDnDModifier() },
        skills = skills.fastMap { it.toDnDSkill() },
        savingThrows = savingThrows.fastMap { it.toDnDSavingThrow() },
        proficiencies = proficiencies.fastMap { it.toJoinProficiency() },
        abilities = abilities.fastMap { it.toAbilityLink() },
        selectionLimits = selectionLimits?.toDnDSelectionLimits(),
        cls = cls?.toClassWithSpells(),
        race = race,
        background = background,
        feat = feat,
        ability = ability,
        spell = spell?.toFullSpell(),
        item = item?.toFullItem(),
        companionEntities = emptyList()
    )
}


