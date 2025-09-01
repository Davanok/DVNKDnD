package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.dice_10
import dvnkdnd.composeapp.generated.resources.dice_100
import dvnkdnd.composeapp.generated.resources.dice_12
import dvnkdnd.composeapp.generated.resources.dice_20
import dvnkdnd.composeapp.generated.resources.dice_4
import dvnkdnd.composeapp.generated.resources.dice_6
import dvnkdnd.composeapp.generated.resources.dice_8
import dvnkdnd.composeapp.generated.resources.dice_other
import org.jetbrains.compose.resources.StringResource

enum class Dices(val faces: Int, val stringRes: StringResource) {
    D4      (4,     Res.string.dice_4),
    D6      (6,     Res.string.dice_6),
    D8      (8,     Res.string.dice_8),
    D10     (10,    Res.string.dice_10),
    D12     (12,    Res.string.dice_12),
    D20     (20,    Res.string.dice_20),
    D100    (100,   Res.string.dice_100),
    OTHER   (0,     Res.string.dice_other)
}