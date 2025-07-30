package com.davanok.dvnkdnd.data.model.entities.dndEntities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSavingThrow
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkill
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.orEmpty
import kotlin.uuid.Uuid

@Immutable
@Serializable
data class DnDFullEntity(
    val id: Uuid = Uuid.random(),
    @SerialName("parent_id")
    val parentId: Uuid?,
    @SerialName("user_id")
    val userId: Uuid?,
    val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String,

    @SerialName("modifier_bonuses")
    val modifierBonuses: List<DnDModifierBonus>,
    val skills: List<DnDSkill>,
    @SerialName("saving_throws")
    val savingThrows: List<DnDSavingThrow>,

    val proficiencies: List<ProficiencyLink>,
    val abilities: List<AbilityLink>,

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
        userId = userId,
        type = type,
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
