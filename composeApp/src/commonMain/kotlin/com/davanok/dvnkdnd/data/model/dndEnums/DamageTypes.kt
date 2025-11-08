package com.davanok.dvnkdnd.data.model.dndEnums

import androidx.compose.ui.graphics.Color
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.damage_type_acid
import dvnkdnd.composeapp.generated.resources.damage_type_bludgeoning
import dvnkdnd.composeapp.generated.resources.damage_type_cold
import dvnkdnd.composeapp.generated.resources.damage_type_fire
import dvnkdnd.composeapp.generated.resources.damage_type_force
import dvnkdnd.composeapp.generated.resources.damage_type_lightning
import dvnkdnd.composeapp.generated.resources.damage_type_necrotic
import dvnkdnd.composeapp.generated.resources.damage_type_piercing
import dvnkdnd.composeapp.generated.resources.damage_type_poison
import dvnkdnd.composeapp.generated.resources.damage_type_psychic
import dvnkdnd.composeapp.generated.resources.damage_type_radiant
import dvnkdnd.composeapp.generated.resources.damage_type_slashing
import dvnkdnd.composeapp.generated.resources.damage_type_thunder
import dvnkdnd.composeapp.generated.resources.magic_school_other
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class DamageTypes(val stringRes: StringResource, val drawableRes: DrawableResource?, val color: Color? = null) { // TODO: drawable nullable is temporary
    ACID        (Res.string.damage_type_acid,           null,                               null),
    BLUDGEONING (Res.string.damage_type_bludgeoning,    null,                               Color(0x8C8C8C)),
    COLD        (Res.string.damage_type_cold,           Res.drawable.damage_type_cold,      Color(0x3399CC)),
    FIRE        (Res.string.damage_type_fire,           Res.drawable.damage_type_fire,      Color(0xEE5500)),
    FORCE       (Res.string.damage_type_force,          null,                               Color(0xCC3333)),
    LIGHTNING   (Res.string.damage_type_lightning,      Res.drawable.damage_type_lightning, Color(0x3366CC)),
    NECROTIC    (Res.string.damage_type_necrotic,       null,                               Color(0x40b050)),
    PIERCING    (Res.string.damage_type_piercing,       null,                               null),
    POISON      (Res.string.damage_type_poison,         Res.drawable.damage_type_poison,    Color(0x44BB00)),
    PSYCHIC     (Res.string.damage_type_psychic,        Res.drawable.damage_type_psychic,   Color(0xCC77AA)),
    RADIANT     (Res.string.damage_type_radiant,        Res.drawable.damage_type_radiant,   Color(0xCCAA00)),
    SLASHING    (Res.string.damage_type_slashing,       null,                               null),
    THUNDER     (Res.string.damage_type_thunder,        Res.drawable.damage_type_thunder,   Color(0x8844BB)),
    OTHER       (Res.string.magic_school_other,         null,                               null)
}