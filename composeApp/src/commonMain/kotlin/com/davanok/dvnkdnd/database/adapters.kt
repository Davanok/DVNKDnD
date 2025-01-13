package com.davanok.dvnkdnd.database

import androidx.room.TypeConverter

class ListIntAdapter {
    @TypeConverter
    fun toListConverter(value: String) = value.split(';').map { it.toInt() }
    @TypeConverter
    fun toStringConverter(value: List<Int>) = value.joinToString(";")
}

