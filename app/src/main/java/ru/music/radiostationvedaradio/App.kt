package ru.music.radiostationvedaradio

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.music.radiostationvedaradio.busines.database.SharedPreferenceProvider
import ru.music.radiostationvedaradio.busines.database.room.AntiHoroDatabase
import ru.music.radiostationvedaradio.utils.myLog

class App : Application() {

    companion object {
        lateinit var db: RoomDatabase
    }

    override fun onCreate() {
        super.onCreate()
        myLog("App onCreate")

        SharedPreferenceProvider.getSharedPreferences(this)

        db = Room.databaseBuilder(this, AntiHoroDatabase::class.java, "AntiHoroDataBase")
            .fallbackToDestructiveMigration()
            .build()

    }

}