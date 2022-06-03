package ru.music.radiostationvedaradio.busines.database.room

import androidx.room.*

@Dao
interface AntiHoroscopeDao {

   //todo Запрос date(id) add later

   @Query("SELECT * FROM anti_goro_id_date")
   suspend fun getHoroEntity() : AntiHoroTodayEntity

   @Query("SELECT * FROM anti_goro_id_date")
   suspend fun checkDate() : AntiHoroTodayEntity
   //todo проверка есть ли запись с текущей датой

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(horoEntity : AntiHoroTodayEntity)

   @Delete
   suspend fun delete(horoEntity : AntiHoroTodayEntity)

}