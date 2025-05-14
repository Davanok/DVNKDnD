package com.davanok.dvnkdnd.data.model.dnd_enums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.ability
import dvnkdnd.composeapp.generated.resources.background
import dvnkdnd.composeapp.generated.resources.cls
import dvnkdnd.composeapp.generated.resources.feat
import dvnkdnd.composeapp.generated.resources.item
import dvnkdnd.composeapp.generated.resources.race
import dvnkdnd.composeapp.generated.resources.spell
import dvnkdnd.composeapp.generated.resources.sub_background
import dvnkdnd.composeapp.generated.resources.sub_class
import dvnkdnd.composeapp.generated.resources.sub_race
import kotlinx.serialization.Serializable

@Serializable
enum class DnDEntityTypes {
    CLASS,
    SUB_CLASS,
    RACE,
    SUB_RACE,
    BACKGROUND,
    SUB_BACKGROUND,
    ABILITY,
    FEAT,
    SPELL,
    ITEM,
}

fun DnDEntityTypes.stringRes() = when(this) {
    DnDEntityTypes.CLASS -> Res.string.cls
    DnDEntityTypes.SUB_CLASS -> Res.string.sub_class
    DnDEntityTypes.RACE -> Res.string.race
    DnDEntityTypes.SUB_RACE -> Res.string.sub_race
    DnDEntityTypes.BACKGROUND -> Res.string.background
    DnDEntityTypes.SUB_BACKGROUND -> Res.string.sub_background
    DnDEntityTypes.ABILITY -> Res.string.ability
    DnDEntityTypes.FEAT -> Res.string.feat
    DnDEntityTypes.SPELL -> Res.string.spell
    DnDEntityTypes.ITEM -> Res.string.item
}