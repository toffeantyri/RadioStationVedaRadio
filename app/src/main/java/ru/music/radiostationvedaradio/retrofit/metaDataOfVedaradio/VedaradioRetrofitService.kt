package ru.music.radiostationvedaradio.retrofit.metaDataOfVedaradio

import android.util.Log
import retrofit2.Call
import retrofit2.http.GET
import ru.music.radiostationvedaradio.retrofit.metaDataOfVedaradio.StreamVedaradioJSONClass

interface VedaradioRetrofitService {
    @GET("/status-json.xsl")
    fun jsonPlease() : Call<String>


}
