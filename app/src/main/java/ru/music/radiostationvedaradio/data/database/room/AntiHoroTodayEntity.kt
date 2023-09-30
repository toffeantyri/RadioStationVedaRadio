package ru.music.radiostationvedaradio.data.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anti_goro_id_date")
data class AntiHoroTodayEntity(

    @PrimaryKey
    val id: Int = 0,

    @ColumnInfo
    val date: String = "",

    @ColumnInfo
    val list: List<String>

)



