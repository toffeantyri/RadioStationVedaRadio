package ru.music.radiostationvedaradio.busines

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.music.radiostationvedaradio.busines.randomnoun.EncodingInterceptor
import ru.music.radiostationvedaradio.busines.randomnoun.HareKrishnaServiceCoro

class ApiProvider {

    private val openVedaradio by lazy { initApi() }
    private val openhtmlRawBody by lazy { initApiRaw() }

    private fun initApi() = Retrofit.Builder()
        //.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
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

    fun provideNounOfToday() : HareKrishnaServiceCoro = openhtmlRawBody.create(HareKrishnaServiceCoro::class.java)


}