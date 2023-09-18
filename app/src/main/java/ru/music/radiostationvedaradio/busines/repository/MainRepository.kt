package ru.music.radiostationvedaradio.busines.repository

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.music.radiostationvedaradio.busines.api.ApiProvider
import ru.music.radiostationvedaradio.busines.database.SharedPreferenceProvider
import ru.music.radiostationvedaradio.utils.parceNounHareKrishnaFromHtml

class MainRepository(api: ApiProvider) : BaseRepository<String>(api) {

    private val prefs by lazy { SharedPreferenceProvider }

    fun reloadNoun(onSuccess: () -> Unit) {
        val randomIntString = "${(1..657).random()}"//на сайте 657 стихов
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d("MyLogRx", "exceptionHandler coro: " + exception.message.toString())
            CoroutineScope(Dispatchers.Main).launch {
                dataEmitter.emit(prefs.loadNoun())
                onSuccess()
            }
        }
        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            val response =
                async(context = Dispatchers.IO) {
                    api.provideNounOfToday().getNewTcitata("http://hare108.ru/bhagavad-gita/$randomIntString.htm")
                }.await()
            if (response.isSuccessful) {
                val noun = response.body()?.parceNounHareKrishnaFromHtml() ?: ""
                prefs.saveNoun(noun)
                withContext(Dispatchers.Main) {
                    dataEmitter.emit(noun)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("MyLogRx", "error noun body" + response.errorBody().toString())
                    dataEmitter.emit(prefs.loadNoun())
                    onSuccess()
                }
            }
        }
    }


}