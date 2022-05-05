package ru.music.radiostationvedaradio.busines

import retrofit2.Call
import retrofit2.http.GET

interface VedaradioRetrofitApiRx {
    @GET("/status-json.xsl")
    fun jsonPlease() //todo RX получатель


}
