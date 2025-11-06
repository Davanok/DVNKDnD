package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.magic_school_abjuration
import dvnkdnd.composeapp.generated.resources.magic_school_conjuration
import dvnkdnd.composeapp.generated.resources.magic_school_divination
import dvnkdnd.composeapp.generated.resources.magic_school_enchantment
import dvnkdnd.composeapp.generated.resources.magic_school_evocation
import dvnkdnd.composeapp.generated.resources.magic_school_illusion
import dvnkdnd.composeapp.generated.resources.magic_school_necromancy
import dvnkdnd.composeapp.generated.resources.magic_school_other
import dvnkdnd.composeapp.generated.resources.magic_school_transmutation
import org.jetbrains.compose.resources.StringResource

enum class MagicSchools(val stringRes: StringResource) {
    ABJURATION(Res.string.magic_school_abjuration),
    CONJURATION(Res.string.magic_school_conjuration),
    DIVINATION(Res.string.magic_school_divination),
    ENCHANTMENT(Res.string.magic_school_enchantment),
    EVOCATION(Res.string.magic_school_evocation),
    ILLUSION(Res.string.magic_school_illusion),
    NECROMANCY(Res.string.magic_school_necromancy),
    TRANSMUTATION(Res.string.magic_school_transmutation),
    OTHER(Res.string.magic_school_other)
}