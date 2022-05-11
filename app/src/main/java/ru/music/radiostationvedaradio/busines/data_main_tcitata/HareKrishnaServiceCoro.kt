package ru.music.radiostationvedaradio.busines.data_main_tcitata

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface HareKrishnaServiceCoro {

        @GET()
   suspend fun getNewTcitata(@Url url : String): Response<String>


}