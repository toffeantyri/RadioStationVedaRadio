package ru.music.radiostationvedaradio.busines.repository

import android.util.Log
import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.busines.api.ApiProvider
import ru.music.radiostationvedaradio.utils.getTodayHoroList


class BadAdviceReposotory(api: ApiProvider) : BaseRepository<List<String>>(api) {


    fun loadNewHoro(date: String, onSuccess: () -> Unit) {

        //todo if date == dateOfOldData(DB) dataEmitter on next oldData
        //todo что бы не плодить запросы в сеть

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d("MyLogRx", "exceptionHandlerCoroutine BARepo : " + exception.message.toString())
            CoroutineScope(Dispatchers.Main).launch {
                //todo load from db
                onSuccess()
            }
        }
        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            val response =
                async(context = Dispatchers.IO) {
                    api.provideAntiHoro().getHoroXML()
                }.await()
            if (response.isSuccessful) {
                val list : List<String> = response.body()?.getTodayHoroList() ?: emptyList()
                //todo if list is not empty save in DB
                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(list)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("MyLogRx", "error noun body" + response.errorBody().toString())
                    //todo load from d
                    onSuccess()
                }
            }
        }
    }


}