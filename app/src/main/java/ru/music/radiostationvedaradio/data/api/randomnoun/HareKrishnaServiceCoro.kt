package ru.music.radiostationvedaradio.data.api.randomnoun

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface HareKrishnaServiceCoro {

    @GET()
    suspend fun getNewTcitata(@Url url: String): Response<String>


}