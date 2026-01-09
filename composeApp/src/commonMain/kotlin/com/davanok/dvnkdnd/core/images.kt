package com.davanok.dvnkdnd.core

import com.davanok.dvnkdnd.domain.entities.DatabaseImage

fun List<DatabaseImage>.getMainImage(): DatabaseImage? =
    firstOrNull { it.isMain } ?: firstOrNull()