package com.davanok.dvnkdnd.data.model.entities

import okio.Path


data class CharacterMin(
    val id: Long,
    val name: String,
    val level: Int,
    val image: Path? = null
)

data class CharacterWithModifiers(
    val id: Long,
    val name: String,
    val characterModifiers: DnDModifiersGroup,
    val clsModifiers: List<DnDModifier>,
    val subClsModifiers: List<DnDModifier>,
    val raceModifiers: List<DnDModifier>,
    val subRaceModifiers: List<DnDModifier>,
    val backgroundModifiers: List<DnDModifier>,
    val subBackgroundModifiers: List<DnDModifier>,
)