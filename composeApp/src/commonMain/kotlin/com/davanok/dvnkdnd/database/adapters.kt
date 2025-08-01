package com.davanok.dvnkdnd.database

import androidx.room.TypeConverter
import com.davanok.dvnkdnd.data.model.dndEnums.SpellComponents
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import okio.Path
import okio.Path.Companion.toPath
import kotlin.uuid.Uuid

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

class ListSpellComponentAdapter {
    @TypeConverter
    fun toListConverter(value: String) = value.split(';').map { component ->
        SpellComponents.entries.first { it.toString()[0] == component[0] }
    }

    @TypeConverter
    fun toStringConverter(value: List<SpellComponents>) = value.joinToString(";") {
        it.toString().first().toString()
    }
}
class EnumListAdapters {
    @TypeConverter
    fun toListConverter(value: String) = value.split(';').map {
        Stats.valueOf(it)
    }
    @TypeConverter
    fun toStringConverter(value: List<Stats>) = value.joinToString { it.name }
}