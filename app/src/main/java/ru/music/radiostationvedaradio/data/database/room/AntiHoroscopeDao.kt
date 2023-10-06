package ru.music.radiostationvedaradio.data.database.room

import androidx.room.*

@Dao
interface AntiHoroscopeDao {


   @Query("SELECT * FROM anti_goro_id_date")
   suspend fun getHoroEntity() : AntiHoroTodayEntity?

   @Query("SELECT * FROM anti_goro_id_date WHERE date = :todayDate ")
   suspend fun getHoroEntityByDate(todayDate : String)  : AntiHoroTodayEntity?


   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(horoEntity : AntiHoroTodayEntity)

   @Delete
   suspend fun delete(horoEntity : AntiHoroTodayEntity)

}