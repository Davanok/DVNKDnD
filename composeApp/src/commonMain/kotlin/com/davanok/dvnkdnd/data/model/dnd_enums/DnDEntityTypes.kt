package com.davanok.dvnkdnd.data.model.dnd_enums

import kotlinx.serialization.Serializable

@Serializable
enum class DnDEntityTypes {
    NONE,
    CLASS,
    SUBCLASS,
    RACE,
    SUBRACE,
    BACKGROUND,
    ABILITY,
    FEAT,
    SPELL,
    ITEM,

}