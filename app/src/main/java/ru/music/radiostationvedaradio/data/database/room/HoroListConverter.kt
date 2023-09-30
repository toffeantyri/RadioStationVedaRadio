package ru.music.radiostationvedaradio.data.database.room

import androidx.room.TypeConverter
import java.util.stream.Collectors


class HoroListConverter {

    @TypeConverter
    fun fromListToString(list: List<String>): String = list.stream().collect(Collectors.joining("|"))

    @TypeConverter
    fun fromStringToList(string: String): List<String> = string.split("|")


}