package ru.music.radiostationvedaradio.busines.database.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AntiHoroTodayEntity::class], version = 1, exportSchema = false)
abstract class AntiHoroDatabase : RoomDatabase() {

    abstract fun getRoomDao(): AntiHoroscopeDao

}