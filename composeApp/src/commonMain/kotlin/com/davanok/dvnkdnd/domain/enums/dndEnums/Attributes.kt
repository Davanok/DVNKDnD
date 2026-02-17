package com.davanok.dvnkdnd.domain.enums.dndEnums

import com.davanok.dvnkdnd.core.UiEntry
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.charisma
import dvnkdnd.composeapp.generated.resources.constitution
import dvnkdnd.composeapp.generated.resources.dexterity
import dvnkdnd.composeapp.generated.resources.intelligence
import dvnkdnd.composeapp.generated.resources.strength
import dvnkdnd.composeapp.generated.resources.wisdom
import org.jetbrains.compose.resources.StringResource

enum class Attributes(override val stringRes: StringResource): UiEntry {
    STRENGTH(Res.string.strength),
    DEXTERITY(Res.string.dexterity),
    CONSTITUTION(Res.string.constitution),
    INTELLIGENCE(Res.string.intelligence),
    WISDOM(Res.string.wisdom),
    CHARISMA(Res.string.charisma);

    fun skills() = Skills.entries.filter { it.attribute == this }
}