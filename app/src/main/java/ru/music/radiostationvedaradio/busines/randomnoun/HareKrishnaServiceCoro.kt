package ru.music.radiostationvedaradio.busines.randomnoun

import retrofit2.Response
import retrofit2.http.*

interface HareKrishnaServiceCoro {

        @GET()
   suspend fun getNewTcitata(@Url url : String): Response<String>


}