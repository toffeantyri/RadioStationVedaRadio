package ru.music.radiostationvedaradio.busines

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import ru.music.radiostationvedaradio.busines.model.metadatavedaradio.StreamVedaradioJSONClass

interface VedaradioRetrofitApiRx {
    @GET("/status-json.xsl")
    suspend fun jsonPlease() : Response<StreamVedaradioJSONClass> //Response<String>


}
