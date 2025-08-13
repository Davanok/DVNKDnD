package com.davanok.dvnkdnd.data.model.entities.character

import okio.Path

data class CharacterShortInfo(
    val name: String = "",
    val image: Path? = null,

    val className: String? = null,
    val subClassName: String? = null,
    val raceName: String? = null,
    val subRaceName: String? = null,
    val backgroundName: String? = null,
    val subBackgroundName: String? = null
){
    fun isBlank() = className.isNullOrBlank() &&
            subClassName.isNullOrBlank() &&
            raceName.isNullOrBlank() &&
            subRaceName.isNullOrBlank() &&
            backgroundName.isNullOrBlank() &&
            subBackgroundName.isNullOrBlank()
}
