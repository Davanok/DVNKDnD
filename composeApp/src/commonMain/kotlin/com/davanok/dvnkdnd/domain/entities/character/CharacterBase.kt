package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class CharacterBase(
    val id: Uuid,
    val userId: Uuid?,
    val name: String,
    val description: String,
    val level: Int,
    val proficiencyBonus: Int? = null,
    val image: String? = null
) {
    fun getProfBonus() = proficiencyBonus ?: proficiencyBonusByLevel(level)
}
