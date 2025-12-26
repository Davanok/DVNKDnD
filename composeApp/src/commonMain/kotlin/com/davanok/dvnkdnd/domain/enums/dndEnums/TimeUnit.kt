package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.time_unit_action
import dvnkdnd.composeapp.generated.resources.time_unit_bonus_action
import dvnkdnd.composeapp.generated.resources.time_unit_long_rest
import dvnkdnd.composeapp.generated.resources.time_unit_minute
import dvnkdnd.composeapp.generated.resources.time_unit_other
import dvnkdnd.composeapp.generated.resources.time_unit_reaction
import dvnkdnd.composeapp.generated.resources.time_unit_ten_minutes
import org.jetbrains.compose.resources.StringResource

enum class TimeUnit(val stringRes: StringResource) {
    ACTION(Res.string.time_unit_action),
    BONUS_ACTION(Res.string.time_unit_bonus_action),
    REACTION(Res.string.time_unit_reaction),
    MINUTE(Res.string.time_unit_minute),
    TEN_MINUTES(Res.string.time_unit_ten_minutes),
    SHORT_REST(Res.string.time_unit_other),
    LONG_REST(Res.string.time_unit_long_rest),
    OTHER(Res.string.time_unit_other),
}