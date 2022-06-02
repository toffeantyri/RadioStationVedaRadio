package ru.music.radiostationvedaradio.busines.database.room

import androidx.room.*

@Dao
interface AntiHoroscopeDao {

   //todo Запрос date(id) add later

   @Query("SELECT * FROM anti_goro_id_date")
   fun getHoroList() : List<String>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(list : List<String>)

   @Delete
   suspend fun delete(list : List<String>)

}