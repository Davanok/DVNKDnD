package com.davanok.dvnkdnd.domain.enums.dndEnums

enum class ValueOperation {
    ADD,                    // Standard bonuses (+1 Sword)
    SET,                    // Overrides (Gauntlets of Ogre Power set STR to 19)
    SET_MIN,                // "Your AC cannot be bigger than 16"
    SET_MAX                // "Your AC cannot be less than 16"
}

enum class RollOperation {
    ADVANTAGE,
    DISADVANTAGE,
    REROLL,                 // Halfling Luck
    CRIT_THRESHOLD_REDUCE   // Fighter Champion (Crit on 19)
}

enum class DamageInteractionType {
    RESISTANCE,             // Half damage
    IMMUNITY,               // Zero damage
    VULNERABILITY           // Double damage
}