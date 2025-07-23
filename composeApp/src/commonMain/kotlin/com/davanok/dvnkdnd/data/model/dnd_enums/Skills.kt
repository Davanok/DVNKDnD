package com.davanok.dvnkdnd.data.model.dnd_enums

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

enum class Skills(val stringRes: StringResource) {
    ATHLETICS(Res.string.athletics),
    ACROBATICS(Res.string.acrobatics),
    SLEIGHT_OF_HAND(Res.string.sleight_of_hand),
    STEALTH(Res.string.stealth),
    ARCANA(Res.string.arcana),
    HISTORY(Res.string.history),
    INVESTIGATION(Res.string.investigation),
    NATURE(Res.string.nature),
    RELIGION(Res.string.religion),
    ANIMAL_HANDLING(Res.string.animal_handling),
    INSIGHT(Res.string.insight),
    MEDICINE(Res.string.medicine),
    PERCEPTION(Res.string.perception),
    SURVIVAL(Res.string.survival),
    DECEPTION(Res.string.deception),
    INTIMIDATION(Res.string.intimidation),
    PERFORMANCE(Res.string.performance),
    PERSUASION(Res.string.persuasion)
}

