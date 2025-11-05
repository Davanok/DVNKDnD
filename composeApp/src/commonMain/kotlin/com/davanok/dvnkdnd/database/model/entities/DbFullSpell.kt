package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.SpellAttackSave
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellAreaInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellAttackLevelModifierInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellAttackSaveInfo

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
        modifiers = modifiers.fastMap(SpellAttackLevelModifier::toSpellAttackLevelModifierInfo),
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
        castingTimeOther = spell.castingTimeOther,
        components = spell.components,
        ritual = spell.ritual,
        materialComponent = spell.materialComponent,
        duration = spell.duration,
        concentration = spell.concentration,
        area = area?.toSpellAreaInfo(),
        attacks = attacks.fastMap(DbFullSpellAttack::toFullSpellAttack)
    )
}