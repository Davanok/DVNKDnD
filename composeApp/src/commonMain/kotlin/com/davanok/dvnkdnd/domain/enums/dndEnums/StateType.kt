package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.state_type_blessing
import dvnkdnd.composeapp.generated.resources.state_type_buff
import dvnkdnd.composeapp.generated.resources.state_type_condition
import dvnkdnd.composeapp.generated.resources.state_type_curse
import dvnkdnd.composeapp.generated.resources.state_type_debuff
import dvnkdnd.composeapp.generated.resources.state_type_exhaustion
import dvnkdnd.composeapp.generated.resources.state_type_other
import org.jetbrains.compose.resources.StringResource

enum class StateType(val stringRes: StringResource) {
    CONDITION   (Res.string.state_type_condition),
    EXHAUSTION  (Res.string.state_type_exhaustion),
    BLESSING    (Res.string.state_type_blessing),
    CURSE       (Res.string.state_type_curse),
    BUFF        (Res.string.state_type_buff),
    DEBUFF      (Res.string.state_type_debuff),
    OTHER       (Res.string.state_type_other)
}