package ru.music.radiostationvedaradio.retrofit.data_main_tcitata

import retrofit2.Call
import retrofit2.http.*

interface HareKrishnaService {

        @GET()
    fun getNewTcitata(@Url url : String): Call<String>


}