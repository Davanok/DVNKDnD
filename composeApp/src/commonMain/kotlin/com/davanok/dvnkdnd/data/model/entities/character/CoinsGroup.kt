package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.Serializable

@Serializable
data class CoinsGroup(
    val copper: Int,
    val silver: Int,
    val electrum: Int,
    val gold: Int,
    val platinum: Int
)