package ru.music.radiostationvedaradio.busines.database.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anti_goro_id_date")
data class AntiHoroTodayEntity (

    @PrimaryKey val date : String = "",
    //@ColumnInfo val list : List<String>



)



