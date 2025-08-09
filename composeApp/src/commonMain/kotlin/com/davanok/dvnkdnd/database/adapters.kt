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
object GenericEnumListAdapters {
    inline fun <reified E: Enum<E>>toListConverter(value: String): List<E> =
        value.split(';').map { enumValueOf<E>(it) }
    fun <E: Enum<E>>toStringConverter(value: List<E>) =
        value.joinToString(";") { it.name }
}
class EnumListAdapters {
    @TypeConverter
    fun spellComponentsToList(value: String): List<SpellComponents> =
        GenericEnumListAdapters.toListConverter(value)
    @TypeConverter
    fun spellComponentsToString(value: List<SpellComponents>): String =
        GenericEnumListAdapters.toStringConverter(value)
    @TypeConverter
    fun statsToList(value: String): List<Stats> =
        GenericEnumListAdapters.toListConverter(value)
    @TypeConverter
    fun statsToString(value: List<Stats>): String =
        GenericEnumListAdapters.toStringConverter(value)
}