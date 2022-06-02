package ru.music.radiostationvedaradio.busines.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.music.radiostationvedaradio.busines.model.antihoro.AntiHoroTodayEntity

@Database(entities = [AntiHoroTodayEntity::class], version = 1, exportSchema = false)
abstract class AntiHoroDatabase : RoomDatabase() {

    abstract fun getRoomDao(): AntiHoroscopeDao

}