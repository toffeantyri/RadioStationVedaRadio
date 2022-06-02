package ru.music.radiostationvedaradio.busines.repository

import android.util.Log
import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.busines.ApiProvider
import ru.music.radiostationvedaradio.busines.SharedPreferenceProvider
import ru.music.radiostationvedaradio.busines.model.antihoro.HoroscopeModelClasses
import ru.music.radiostationvedaradio.utils.parceNounHareKrishnaFromHtml

class BadAdviceReposotory(api: ApiProvider) : BaseRepository<List<String>>(api) {


    fun loadNewHoro(date: String, onSuccess: () -> Unit) {

        //todo if date == dateOfOldData(DB) dataEmitter on next oldData
        //todo что бы не плодить запросы в сеть

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d("MyLogRx", "exceptionHandler coro: " + exception.message.toString())
            CoroutineScope(Dispatchers.Main).launch {
                //todo
                onSuccess()
            }
        }
        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            val response =
                async(context = Dispatchers.IO) {
                    api.provideAntiHoro().getHoroXML()
                }.await()
            if (response.isSuccessful) {
                //val list : List<String> = response.body()
                //todo
                withContext(Dispatchers.Main) {
                    //dataEmitter.onNext(noun)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("MyLogRx", "error noun body" + response.errorBody().toString())
                    //todo
                    onSuccess()
                }
            }
        }
    }


}