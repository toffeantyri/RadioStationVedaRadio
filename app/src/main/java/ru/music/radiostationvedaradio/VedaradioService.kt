package ru.music.radiostationvedaradio

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VedaradioService {

    @GET("/status-json.xsl")
    fun jsonPlease() : Call<StreamVedaradioJSONClass>
}
