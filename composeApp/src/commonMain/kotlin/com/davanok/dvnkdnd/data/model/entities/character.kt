@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.model.entities

import okio.Path
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


data class CharacterMin(
    val id: Uuid,
    val name: String,
    val level: Int,
    val image: Path? = null
)

data class CharacterWithModifiers(
    val character: CharacterMin,
    val characterModifiers: DnDModifiersGroup,
    val clsModifiers: List<DnDModifier>,
    val subClsModifiers: List<DnDModifier>,
    val raceModifiers: List<DnDModifier>,
    val subRaceModifiers: List<DnDModifier>,
    val backgroundModifiers: List<DnDModifier>,
    val subBackgroundModifiers: List<DnDModifier>,
)