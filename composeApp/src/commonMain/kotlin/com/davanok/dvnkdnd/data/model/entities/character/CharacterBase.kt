package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.util.proficiencyBonusByLevel
import com.davanok.dvnkdnd.database.entities.character.Character
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import okio.Path
import kotlin.uuid.Uuid


@Serializable
data class CharacterBase(
    val id: Uuid,
    val userId: Uuid?,
    val name: String,
    val description: String,
    val level: Int,
    val proficiencyBonus: Int? = null,
    @Transient
    val image: Path? = null
) {
    fun getProfBonus() = proficiencyBonus ?: proficiencyBonusByLevel(level)
}
fun Character.toCharacterBase() = CharacterBase(
    id = id,
    userId = userId,
    name = name,
    description = description,
    level = level,
    proficiencyBonus = proficiencyBonus,
    image = image
)
fun CharacterBase.toCharacter() = Character(
    id = id,
    userId = userId,
    name = name,
    description = description,
    level = level,
    proficiencyBonus = proficiencyBonus,
    image = image
)