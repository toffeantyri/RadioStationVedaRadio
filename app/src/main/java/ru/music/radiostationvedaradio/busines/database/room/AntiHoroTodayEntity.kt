package ru.music.radiostationvedaradio.busines.database.room

import androidx.room.*

@Entity(tableName = "anti_goro_id_date")
data class AntiHoroTodayEntity(

    @PrimaryKey
    val id: Int = 0,

    @ColumnInfo
    val date: String = "",

    @ColumnInfo
    val list: List<String>

)



