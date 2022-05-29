package ru.music.radiostationvedaradio.busines.randomnoun

import retrofit2.Call
import retrofit2.http.*

interface HareKrishnaService {

        @GET()
    fun getNewTcitata(@Url url : String): Call<String>


}