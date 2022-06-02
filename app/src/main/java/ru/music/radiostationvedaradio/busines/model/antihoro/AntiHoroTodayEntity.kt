package ru.music.radiostationvedaradio.busines.model.antihoro

import androidx.annotation.ArrayRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anti_goro_id_date")
data class AntiHoroTodayEntity (

    @PrimaryKey val date : String = "",
    @ColumnInfo val list : List<String>

    //todo list не хранить надо примитив либо Embded вроде

)



