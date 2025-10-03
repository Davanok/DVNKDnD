package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.Serializable

@Serializable
data class CoinsGroup(
    val copper: Int = 0,
    val silver: Int = 0,
    val electrum: Int = 0,
    val gold: Int = 0,
    val platinum: Int = 0
)