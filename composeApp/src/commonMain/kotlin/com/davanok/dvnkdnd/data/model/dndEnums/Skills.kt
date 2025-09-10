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

enum class Skills(val stat: Attributes, val stringRes: StringResource) {
    ATHLETICS       (Attributes.STRENGTH,     Res.string.athletics),
    ACROBATICS      (Attributes.DEXTERITY,    Res.string.acrobatics),
    SLEIGHT_OF_HAND (Attributes.DEXTERITY,    Res.string.sleight_of_hand),
    STEALTH         (Attributes.DEXTERITY,    Res.string.stealth),
    ARCANA          (Attributes.INTELLIGENCE, Res.string.arcana),
    HISTORY         (Attributes.INTELLIGENCE, Res.string.history),
    INVESTIGATION   (Attributes.INTELLIGENCE, Res.string.investigation),
    NATURE          (Attributes.INTELLIGENCE, Res.string.nature),
    RELIGION        (Attributes.INTELLIGENCE, Res.string.religion),
    ANIMAL_HANDLING (Attributes.WISDOM,       Res.string.animal_handling),
    INSIGHT         (Attributes.WISDOM,       Res.string.insight),
    MEDICINE        (Attributes.WISDOM,       Res.string.medicine),
    PERCEPTION      (Attributes.WISDOM,       Res.string.perception),
    SURVIVAL        (Attributes.WISDOM,       Res.string.survival),
    DECEPTION       (Attributes.CHARISMA,     Res.string.deception),
    INTIMIDATION    (Attributes.CHARISMA,     Res.string.intimidation),
    PERFORMANCE     (Attributes.CHARISMA,     Res.string.performance),
    PERSUASION      (Attributes.CHARISMA,     Res.string.persuasion)
}

