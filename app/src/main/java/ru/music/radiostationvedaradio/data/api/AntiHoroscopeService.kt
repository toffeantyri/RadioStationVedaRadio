package ru.music.radiostationvedaradio.data.api


import retrofit2.Response
import retrofit2.http.GET
import ru.music.radiostationvedaradio.data.model.antihoro.HoroscopeModelClasses

interface AntiHoroscopeService {

    @GET("/r/export/utf/xml/daily/anti.xml")
    suspend fun getHoroXML() : Response<HoroscopeModelClasses>

}