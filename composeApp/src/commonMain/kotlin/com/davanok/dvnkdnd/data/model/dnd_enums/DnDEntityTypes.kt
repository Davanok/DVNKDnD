package com.davanok.dvnkdnd.data.model.dnd_enums

import kotlinx.serialization.Serializable

@Serializable
enum class DnDEntityTypes {
    NONE,
    CLASS,
    RACE,
    BACKGROUND
}

fun DnDEntityTypes.tableName(): String =
    when(this){
        DnDEntityTypes.CLASS -> "DnDClasses"
        DnDEntityTypes.RACE -> "DnDClasses"
        DnDEntityTypes.BACKGROUND -> "DnDClasses"
        else -> throw IllegalArgumentException("DnDEntityTypes.$name does not have table name")
    }