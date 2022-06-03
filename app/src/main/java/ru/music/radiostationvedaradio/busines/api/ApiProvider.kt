package ru.music.radiostationvedaradio.busines.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.music.radiostationvedaradio.busines.api.randomnoun.EncodingInterceptor
import ru.music.radiostationvedaradio.busines.api.randomnoun.HareKrishnaServiceCoro

class ApiProvider {

    private val openVedaradio by lazy { initApi() }
    private val openhtmlRawBody by lazy { initApiRaw() }
    private val openXmlBody by lazy { initXmlApi() }

    private fun initApi() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://stream.vedaradio.fm")
        .build()

    private fun initApiRaw() = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(EncodingInterceptor()).build()
        )
        .baseUrl("http://hare108.ru")
        .build()

    private fun initXmlApi() = Retrofit.Builder()
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .baseUrl("https://ignio.com")
        .build()




    fun provideNounOfToday() : HareKrishnaServiceCoro = openhtmlRawBody.create(HareKrishnaServiceCoro::class.java)

    fun provideAntiHoro() : AntiHoroscopeService = openXmlBody.create(AntiHoroscopeService::class.java)


}