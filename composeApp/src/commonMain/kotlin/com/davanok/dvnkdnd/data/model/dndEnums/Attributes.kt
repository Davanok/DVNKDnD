package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.charisma
import dvnkdnd.composeapp.generated.resources.constitution
import dvnkdnd.composeapp.generated.resources.dexterity
import dvnkdnd.composeapp.generated.resources.intelligence
import dvnkdnd.composeapp.generated.resources.strength
import dvnkdnd.composeapp.generated.resources.wisdom
import org.jetbrains.compose.resources.StringResource

enum class Attributes(val stringRes: StringResource) {
    STRENGTH(Res.string.strength),
    DEXTERITY(Res.string.dexterity),
    CONSTITUTION(Res.string.constitution),
    INTELLIGENCE(Res.string.intelligence),
    WISDOM(Res.string.wisdom),
    CHARISMA(Res.string.charisma)
}