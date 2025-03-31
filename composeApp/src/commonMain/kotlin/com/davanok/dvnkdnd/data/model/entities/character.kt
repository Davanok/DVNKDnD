package com.davanok.dvnkdnd.data.model.entities


data class CharacterMin(
    val id: Long,
    val name: String,
    val level: Int,
    val image: String? = null, // TODO: replace to Path?, add String to Path adapter
)