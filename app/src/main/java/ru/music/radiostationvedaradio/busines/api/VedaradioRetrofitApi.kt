package ru.music.radiostationvedaradio.busines.api

import retrofit2.Call
import retrofit2.http.GET

interface VedaradioRetrofitApi {
    @GET("/status-json.xsl")
    fun jsonPlease() : Call<String>


}
