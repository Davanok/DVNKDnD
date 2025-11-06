package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.spell_component_author
import dvnkdnd.composeapp.generated.resources.spell_component_author_short
import dvnkdnd.composeapp.generated.resources.spell_component_material
import dvnkdnd.composeapp.generated.resources.spell_component_material_short
import dvnkdnd.composeapp.generated.resources.spell_component_other
import dvnkdnd.composeapp.generated.resources.spell_component_other_short
import dvnkdnd.composeapp.generated.resources.spell_component_somatic
import dvnkdnd.composeapp.generated.resources.spell_component_somatic_short
import dvnkdnd.composeapp.generated.resources.spell_component_verbal
import dvnkdnd.composeapp.generated.resources.spell_component_verbal_short
import org.jetbrains.compose.resources.StringResource

enum class SpellComponents(val stringRes: StringResource, val stringResShort: StringResource) {
    VERBAL(Res.string.spell_component_verbal, Res.string.spell_component_verbal_short),
    SOMATIC(Res.string.spell_component_somatic, Res.string.spell_component_somatic_short),
    MATERIAL(Res.string.spell_component_material, Res.string.spell_component_material_short),
    AUTHOR(Res.string.spell_component_author, Res.string.spell_component_author_short),
    OTHER(Res.string.spell_component_other, Res.string.spell_component_other_short)
}