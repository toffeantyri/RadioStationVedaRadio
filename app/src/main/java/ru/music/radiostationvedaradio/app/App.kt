package ru.music.radiostationvedaradio.app

import android.app.Application
import androidx.room.Room
import ru.music.radiostationvedaradio.data.database.SharedPreferenceProvider
import ru.music.radiostationvedaradio.data.database.room.AntiHoroDatabase
import ru.music.radiostationvedaradio.utils.myLog

class App : Application() {

    companion object {
        lateinit var db: AntiHoroDatabase
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