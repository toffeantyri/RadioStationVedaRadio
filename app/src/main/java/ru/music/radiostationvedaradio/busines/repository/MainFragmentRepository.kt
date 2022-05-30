package ru.music.radiostationvedaradio.busines.repository

import android.util.Log
import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.busines.ApiProvider
import ru.music.radiostationvedaradio.utils.parceNounHareKrishnaFromHtml

class MainFragmentRepository(api: ApiProvider) : BaseRepository<String>(api) {

    fun reloadNoun(onSuccess: () -> Unit) {
        val randomIntString = "${(1..657).random()}"//на сайте 657 стихов
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d("MyLogRx", "exceptionHandler coro: " + exception.message.toString())
            /*todo грузим из БД*/
            CoroutineScope(Dispatchers.Main).launch { onSuccess() }
        }
        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            val response =
                async(context = Dispatchers.IO) {
                    api.provideNounOfToday().getNewTcitata("http://hare108.ru/bhagavad-gita/$randomIntString.htm")
                }.await()
            if (response.isSuccessful) {
                val noun = response.body()?.parceNounHareKrishnaFromHtml() ?: ""

                /*todo сохраняем в БД*/

                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(noun)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("MyLogRx", "error noun body" + response.errorBody().toString())
                    /*todo грузим из БД*/
                    onSuccess()
                }
            }
        }
    }
}