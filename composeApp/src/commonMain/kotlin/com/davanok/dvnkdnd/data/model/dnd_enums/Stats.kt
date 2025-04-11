package com.davanok.dvnkdnd.data.model.dnd_enums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.charisma
import dvnkdnd.composeapp.generated.resources.constitution
import dvnkdnd.composeapp.generated.resources.dexterity
import dvnkdnd.composeapp.generated.resources.intelligence
import dvnkdnd.composeapp.generated.resources.strength
import dvnkdnd.composeapp.generated.resources.wisdom

enum class Stats {
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA
}

fun Stats.stringRes() = when(this) {
    Stats.STRENGTH -> Res.string.strength
    Stats.DEXTERITY -> Res.string.dexterity
    Stats.CONSTITUTION -> Res.string.constitution
    Stats.INTELLIGENCE -> Res.string.intelligence
    Stats.WISDOM -> Res.string.wisdom
    Stats.CHARISMA -> Res.string.charisma
}