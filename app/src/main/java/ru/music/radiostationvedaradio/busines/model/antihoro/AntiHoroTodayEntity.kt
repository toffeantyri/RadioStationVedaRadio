package ru.music.radiostationvedaradio.busines.model.antihoro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anti_goro_today")
data class AntiHoroTodayEntity (
    @PrimaryKey(autoGenerate = true) val id : Int = 0


// todo column or other
)



