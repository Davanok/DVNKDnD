package com.davanok.dvnkdnd.data.model.dndEnums

enum class CasterProgression {
    FULL,       // bard, wizard, druid - all spell slots
    HALF_PLUS,  // artificer - half spell slots rounded up
    HALF,       // paladin, ranger - half spell slots  rounded down
    THIRD,      // fighter, rogue with subclass - third spell slots
    NONE,       // fighter, rogue
    OTHER       // warlock, monk - not included
}