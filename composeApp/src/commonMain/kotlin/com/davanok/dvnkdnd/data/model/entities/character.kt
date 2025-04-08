package com.davanok.dvnkdnd.data.model.entities

import androidx.room.ColumnInfo
import okio.Path
import kotlin.uuid.Uuid


data class CharacterMin(
    val id: Uuid,
    val name: String,
    val level: Int,
    @ColumnInfo("main_image")
    val image: Path? = null
)

data class CharacterWithModifiers(
    val character: CharacterMin,
    val characterModifiers: DnDModifiersGroup?,

    val classesWithModifiers: Map<DnDEntityMin, List<DnDModifier>>,

    val raceModifiers: List<DnDModifier>,
    val subRaceModifiers: List<DnDModifier>,
    val backgroundModifiers: List<DnDModifier>,
    val subBackgroundModifiers: List<DnDModifier>,
)