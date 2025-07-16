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

enum class Skills {
    ATHLETICS, // strength
    ACROBATICS, SLEIGHT_OF_HAND, STEALTH, // dexterity
    ARCANA, HISTORY, INVESTIGATION, NATURE, RELIGION, // intelligence
    ANIMAL_HANDLING, INSIGHT, MEDICINE, PERCEPTION, SURVIVAL, // wisdom
    DECEPTION, INTIMIDATION, PERFORMANCE, PERSUASION, // charisma
}
fun Skills.stringRes() = when(this) {
    Skills.ATHLETICS -> Res.string.athletics
    Skills.ACROBATICS -> Res.string.acrobatics
    Skills.SLEIGHT_OF_HAND -> Res.string.sleight_of_hand
    Skills.STEALTH -> Res.string.stealth
    Skills.ARCANA -> Res.string.arcana
    Skills.HISTORY -> Res.string.history
    Skills.INVESTIGATION -> Res.string.investigation
    Skills.NATURE -> Res.string.nature
    Skills.RELIGION -> Res.string.religion
    Skills.ANIMAL_HANDLING -> Res.string.animal_handling
    Skills.INSIGHT -> Res.string.insight
    Skills.MEDICINE -> Res.string.medicine
    Skills.PERCEPTION -> Res.string.perception
    Skills.SURVIVAL -> Res.string.survival
    Skills.DECEPTION -> Res.string.deception
    Skills.INTIMIDATION -> Res.string.intimidation
    Skills.PERFORMANCE -> Res.string.performance
    Skills.PERSUASION -> Res.string.persuasion
}
