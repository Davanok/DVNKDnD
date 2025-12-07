package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.casting_time_action
import dvnkdnd.composeapp.generated.resources.casting_time_bonus_action
import dvnkdnd.composeapp.generated.resources.casting_time_minute
import dvnkdnd.composeapp.generated.resources.casting_time_other
import dvnkdnd.composeapp.generated.resources.casting_time_reaction
import dvnkdnd.composeapp.generated.resources.casting_time_ten_minutes
import org.jetbrains.compose.resources.StringResource

enum class CastingTime(val stringRes: StringResource) {
    ACTION(Res.string.casting_time_action),
    BONUS_ACTION(Res.string.casting_time_bonus_action),
    REACTION(Res.string.casting_time_reaction),
    MINUTE(Res.string.casting_time_minute),
    TEN_MINUTES(Res.string.casting_time_ten_minutes),
    OTHER(Res.string.casting_time_other)
}