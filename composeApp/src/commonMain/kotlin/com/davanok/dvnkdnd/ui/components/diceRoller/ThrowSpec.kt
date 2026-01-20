package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices

@Immutable
data class ThrowSpec(
    val dice: Dices,
    val count: Int,
    val modifier: Any? = null // Can now be Int, String, or a Custom Object
)
