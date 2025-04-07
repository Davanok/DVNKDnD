package com.davanok.dvnkdnd.database

import androidx.room.TypeConverter
import okio.Path
import okio.Path.Companion.toPath
import kotlin.uuid.Uuid

@Suppress("unused")
class MainAdapters {
    @TypeConverter
    fun stringToListConverter(value: String) = value.split(';').map { it.toInt() }
    @TypeConverter
    fun listToStringConverter(value: List<Int>) = value.joinToString(";")

    @TypeConverter
    fun stringToPathConverter(value: String) = value.toPath()
    @TypeConverter
    fun pathToStringConverter(value: Path) = value.toString()

    @TypeConverter
    fun uuidToBytes(value: Uuid) = value.toByteArray()
    @TypeConverter
    fun bytesToUuid(value: ByteArray) = Uuid.fromByteArray(value)
}

