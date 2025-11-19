package com.davanok.dvnkdnd.database.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbProficiency
import com.davanok.dvnkdnd.database.model.adapters.entities.toAbilityRegain
import com.davanok.dvnkdnd.database.model.adapters.entities.toProficiency

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
        regains = regains.map(DbAbilityRegain::toAbilityRegain)
    )
}