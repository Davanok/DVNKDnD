package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.AbilityInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.model.adapters.entities.toAbilityRegain
import com.davanok.dvnkdnd.database.model.adapters.entities.toProficiency

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
        regains = regains.fastMap(DnDAbilityRegain::toAbilityRegain)
    )
}