package com.davanok.dvnkdnd.data.model.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.acrobatics
import dvnkdnd.composeapp.generated.resources.animal_handling
import dvnkdnd.composeapp.generated.resources.arcana
import dvnkdnd.composeapp.generated.resources.athletics
import dvnkdnd.composeapp.generated.resources.deception
import dvnkdnd.composeapp.generated.resources.history
import dvnkdnd.composeapp.generated.resources.insight
import dvnkdnd.composeapp.generated.resources.intimidation
import dvnkdnd.composeapp.generated.resources.investigation
import dvnkdnd.composeapp.generated.resources.medicine
import dvnkdnd.composeapp.generated.resources.nature
import dvnkdnd.composeapp.generated.resources.perception
import dvnkdnd.composeapp.generated.resources.performance
import dvnkdnd.composeapp.generated.resources.persuasion
import dvnkdnd.composeapp.generated.resources.religion
import dvnkdnd.composeapp.generated.resources.sleight_of_hand
import dvnkdnd.composeapp.generated.resources.stealth
import dvnkdnd.composeapp.generated.resources.survival
import org.jetbrains.compose.resources.StringResource

enum class Skills(val stat: Stats, val stringRes: StringResource) {
    ATHLETICS       (Stats.STRENGTH,     Res.string.athletics),
    ACROBATICS      (Stats.DEXTERITY,    Res.string.acrobatics),
    SLEIGHT_OF_HAND (Stats.DEXTERITY,    Res.string.sleight_of_hand),
    STEALTH         (Stats.DEXTERITY,    Res.string.stealth),
    ARCANA          (Stats.INTELLIGENCE, Res.string.arcana),
    HISTORY         (Stats.INTELLIGENCE, Res.string.history),
    INVESTIGATION   (Stats.INTELLIGENCE, Res.string.investigation),
    NATURE          (Stats.INTELLIGENCE, Res.string.nature),
    RELIGION        (Stats.INTELLIGENCE, Res.string.religion),
    ANIMAL_HANDLING (Stats.WISDOM,       Res.string.animal_handling),
    INSIGHT         (Stats.WISDOM,       Res.string.insight),
    MEDICINE        (Stats.WISDOM,       Res.string.medicine),
    PERCEPTION      (Stats.WISDOM,       Res.string.perception),
    SURVIVAL        (Stats.WISDOM,       Res.string.survival),
    DECEPTION       (Stats.CHARISMA,     Res.string.deception),
    INTIMIDATION    (Stats.CHARISMA,     Res.string.intimidation),
    PERFORMANCE     (Stats.CHARISMA,     Res.string.performance),
    PERSUASION      (Stats.CHARISMA,     Res.string.persuasion)
}

