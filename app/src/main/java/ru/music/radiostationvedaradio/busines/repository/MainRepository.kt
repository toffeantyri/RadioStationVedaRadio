package ru.music.radiostationvedaradio.busines.repository

import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Call
import ru.music.radiostationvedaradio.busines.ApiProvider

class MainRepository(api: ApiProvider) : BaseRepository<String>(api) {


   fun reloadMetaData() {
        /*
       CoroutineScope(Dispatchers.IO).launch {
            val call = async { api.provideVedaRadioMetaData().jsonPlease() }.await()
            if (call.isSuccessful) {
                Log.d("MyLogRx", " metadata successful : ${call.body()?.icestats?.source?.get(0)?.title}")
            } else {
                Log.d("MyLogRx", "error Metadata body" + call.errorBody().toString())
            }
        }
        */

    }

}