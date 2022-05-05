package ru.music.radiostationvedaradio.busines

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiProvider {

    private val openVedaradio by lazy { initApi() }

    private fun initApi() = Retrofit.Builder()
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://stream.vedaradio.fm")
        .build()

    fun provideVedaRadioMetaData() : VedaradioRetrofitApiRx = openVedaradio.create(VedaradioRetrofitApiRx::class.java)

}