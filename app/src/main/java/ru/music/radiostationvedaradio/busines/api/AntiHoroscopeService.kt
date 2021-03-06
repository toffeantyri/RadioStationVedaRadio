package ru.music.radiostationvedaradio.busines.api


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import ru.music.radiostationvedaradio.busines.model.antihoro.HoroscopeModelClasses

interface AntiHoroscopeService {

    @GET("/r/export/utf/xml/daily/anti.xml")
    suspend fun getHoroXML() : Response<HoroscopeModelClasses>

}