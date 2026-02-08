package com.davanok.dvnkdnd.domain.enums.dndEnums

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
    ACID        (Res.string.damage_type_acid,           Res.drawable.damage_type_acid,      Color(0xFF80B000)),
    BLUDGEONING (Res.string.damage_type_bludgeoning,    null,                               null),
    COLD        (Res.string.damage_type_cold,           Res.drawable.damage_type_cold,      Color(0xFF3399CC)),
    FIRE        (Res.string.damage_type_fire,           Res.drawable.damage_type_fire,      Color(0xFFEE5500)),
    FORCE       (Res.string.damage_type_force,          Res.drawable.damage_type_force,     Color(0xFFCC3333)),
    LIGHTNING   (Res.string.damage_type_lightning,      Res.drawable.damage_type_lightning, Color(0xFF3366CC)),
    NECROTIC    (Res.string.damage_type_necrotic,       Res.drawable.damage_type_necrotic,  Color(0xFF40b050)),
    PIERCING    (Res.string.damage_type_piercing,       null,                               null),
    POISON      (Res.string.damage_type_poison,         Res.drawable.damage_type_poison,    Color(0xFF44BB00)),
    PSYCHIC     (Res.string.damage_type_psychic,        Res.drawable.damage_type_psychic,   Color(0xFFCC77AA)),
    RADIANT     (Res.string.damage_type_radiant,        Res.drawable.damage_type_radiant,   Color(0xFFCCAA00)),
    SLASHING    (Res.string.damage_type_slashing,       null,                               null),
    THUNDER     (Res.string.damage_type_thunder,        Res.drawable.damage_type_thunder,   Color(0xFF8844BB)),
    OTHER       (Res.string.magic_school_other,         null,                               null)
}