package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.dice_10
import dvnkdnd.composeapp.generated.resources.dice_100
import dvnkdnd.composeapp.generated.resources.dice_12
import dvnkdnd.composeapp.generated.resources.dice_20
import dvnkdnd.composeapp.generated.resources.dice_4
import dvnkdnd.composeapp.generated.resources.dice_6
import dvnkdnd.composeapp.generated.resources.dice_8
import dvnkdnd.composeapp.generated.resources.dice_d10
import dvnkdnd.composeapp.generated.resources.dice_d100
import dvnkdnd.composeapp.generated.resources.dice_d12
import dvnkdnd.composeapp.generated.resources.dice_d20
import dvnkdnd.composeapp.generated.resources.dice_d4
import dvnkdnd.composeapp.generated.resources.dice_d6
import dvnkdnd.composeapp.generated.resources.dice_d8
import dvnkdnd.composeapp.generated.resources.dice_other
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Dices(val faces: Int, val stringRes: StringResource, val drawableRes: DrawableResource) {
    D4      (4,     Res.string.dice_4,      Res.drawable.dice_d4),
    D6      (6,     Res.string.dice_6,      Res.drawable.dice_d6),
    D8      (8,     Res.string.dice_8,      Res.drawable.dice_d8),
    D10     (10,    Res.string.dice_10,     Res.drawable.dice_d10),
    D12     (12,    Res.string.dice_12,     Res.drawable.dice_d12),
    D20     (20,    Res.string.dice_20,     Res.drawable.dice_d20),
    D100    (100,   Res.string.dice_100,    Res.drawable.dice_d100),
    OTHER   (1,     Res.string.dice_other,  Res.drawable.dice_d20)
}