package ru.music.radiostationvedaradio.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AntiHoroTodayEntity::class], version = 1, exportSchema = false)
@TypeConverters(HoroListConverter::class)
abstract class AntiHoroDatabase : RoomDatabase() {

    abstract fun getRoomDao(): AntiHoroscopeDao

}