package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.util.proficiencyBonusByLevel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import okio.Path
import kotlin.uuid.Uuid


@Serializable
data class CharacterMin(
    val id: Uuid,
    val userId: Uuid?,
    val name: String,
    val level: Int,
    val proficiencyBonus: Int? = null,
    @Transient
    val image: Path? = null
) {
    fun getProfBonus() = proficiencyBonus ?: proficiencyBonusByLevel(level)
}
