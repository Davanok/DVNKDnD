package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbSpellAttackSave
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellAreaInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellAttackLevelModifierInfo
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellAttackSaveInfo

data class DbFullSpellAttack(
    @Embedded
    val attack: DbSpellAttack,
    @Relation(
        parentColumn = "id",
        entityColumn = "attack_id"
    )
    val modifiers: List<DbSpellAttackLevelModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val save: DbSpellAttackSave?
) {
    fun toFullSpellAttack() = FullSpellAttack(
        id = attack.id,
        damageType = attack.damageType,
        diceCount = attack.diceCount,
        dice = attack.dice,
        modifier = attack.modifier,
        modifiers = modifiers.fastMap(DbSpellAttackLevelModifier::toSpellAttackLevelModifierInfo),
        save = save?.toSpellAttackSaveInfo()
    )
}


data class DbFullSpell(
    @Embedded
    val spell: DbSpell,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val area: DbSpellArea?,
    @Relation(
        DbSpellAttack::class,
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
        components = spell.components.toSet(),
        ritual = spell.ritual,
        materialComponent = spell.materialComponent,
        duration = spell.duration,
        concentration = spell.concentration,
        area = area?.toSpellAreaInfo(),
        attacks = attacks.fastMap(DbFullSpellAttack::toFullSpellAttack)
    )
}