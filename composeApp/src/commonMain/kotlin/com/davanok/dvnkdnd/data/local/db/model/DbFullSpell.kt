package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellArea
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttack
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttackLevelModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbSpellAttackSave

data class DbFullSpellAttack(
    @Embedded
    val attack: DbSpellAttack,
    @Relation(
        parentColumn = "id",
        entityColumn = "attack_id"
    )
    val levelModifiers: List<DbSpellAttackLevelModifier>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val save: DbSpellAttackSave?
)


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
)