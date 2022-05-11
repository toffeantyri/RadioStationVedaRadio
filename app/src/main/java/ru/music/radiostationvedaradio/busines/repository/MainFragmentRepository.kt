package ru.music.radiostationvedaradio.busines.repository

import android.util.Log
import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.busines.ApiProvider

class MainFragmentRepository(api : ApiProvider) : BaseRepository<String>(api) {

    fun reloadNoun() {
        val randomIntString = "${(1..657).random()}"
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.d("MyLogRx", "exceptionHandler coro: " + exception.message.toString())
            /*todo грузим из БД*/
        }

        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            val response =
                async (context = Dispatchers.IO) {
                    api.provideNounOfToday().getNewTcitata("http://hare108.ru/bhagavad-gita/$randomIntString.htm")
                }.await()

            if (response.isSuccessful) {
                val regexpLine = "\".[^a-z]{50,1500}\"".trimMargin()
                val found = regexpLine.toRegex().find(response.body().toString())
                val formattedText = found?.value?.replace(". ", ".\n\n")
                val formattedText2 = formattedText?.replace("\"", " ")

                /*todo сохраняем в БД*/

                withContext(Dispatchers.Main){
                    dataEmitter.onNext(formattedText2)
                }


            } else {
                withContext(Dispatchers.Main){
                    Log.d("MyLogRx", "error noun body" + response.errorBody().toString())
                    /*todo грузим из БД*/
                }



            }
        }
    }

}