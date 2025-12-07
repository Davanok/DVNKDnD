package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.domain.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.data.local.mappers.entities.toAbilityRegain
import com.davanok.dvnkdnd.data.local.mappers.entities.toProficiency

data class DbJoinProficiency(
    @Embedded val link: DbEntityProficiency,
    @Relation(
        parentColumn = "proficiency_id",
        entityColumn = "id"
    )
    val proficiency: DbProficiency
) {
    fun toJoinProficiency() = JoinProficiency(
        level = link.level,
        proficiency = proficiency.toProficiency()
    )
}

data class DbAbilityInfo(
    @Embedded val ability: DbAbility,
    @Relation(
        parentColumn = "id",
        entityColumn = "ability_id"
    )
    val regains: List<DbAbilityRegain>
) {
    fun toAbilityInfo() = AbilityInfo(
        usageLimitByLevel = ability.usageLimitByLevel,
        regains = regains.map(DbAbilityRegain::toAbilityRegain),
        givesStateSelf = ability.givesStateSelf,
        givesStateTarget = ability.givesStateTarget
    )
}