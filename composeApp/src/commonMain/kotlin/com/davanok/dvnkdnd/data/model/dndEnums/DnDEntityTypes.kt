package com.davanok.dvnkdnd.data.model.dndEnums

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
import org.jetbrains.compose.resources.StringResource

enum class DnDEntityTypes(val stringRes: StringResource) {
    CLASS(Res.string.cls),
    SUB_CLASS(Res.string.sub_class),
    RACE(Res.string.race),
    SUB_RACE(Res.string.sub_race),
    BACKGROUND(Res.string.background),
    SUB_BACKGROUND(Res.string.sub_background),
    ABILITY(Res.string.ability),
    FEAT(Res.string.feat),
    SPELL(Res.string.spell),
    ITEM(Res.string.item),
}