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

data class CharacterWithAllModifiers(
    val character: CharacterMin,
    val characterStats: DnDModifiersGroup?,

    val selectedModifiers: List<Uuid>,
    val classes: List<DnDEntityWithModifiers>,
    val race: DnDEntityWithModifiers?,
    val subRace: DnDEntityWithModifiers?,
    val background: DnDEntityWithModifiers?,
    val subBackground: DnDEntityWithModifiers?,
)