package ru.music.radiostationvedaradio.busines.repository


import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.App
import ru.music.radiostationvedaradio.busines.api.ApiProvider
import ru.music.radiostationvedaradio.busines.database.room.AntiHoroscopeDao
import ru.music.radiostationvedaradio.utils.getTodayHoroList
import ru.music.radiostationvedaradio.utils.listHoroToEntity
import ru.music.radiostationvedaradio.utils.myLogNet


class BadAdviceReposotory(api: ApiProvider) : BaseRepository<List<String>>(api) {

    private val databaseDao: AntiHoroscopeDao = App.Companion.db.getRoomDao()

    fun loadNewHoro(date: String, onSuccess: () -> Unit) {

        //todo if date == dateOfOldData(DB) dataEmitter on next oldData
        //todo что бы не плодить запросы в сеть

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            myLogNet("exceptionHandlerCoroutine BARepo : " + exception.message.toString())
            CoroutineScope(Dispatchers.Main).launch {
                val list = databaseDao.getHoroEntity().list
                dataEmitter.onNext(list)
                onSuccess()
            }
        }
        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            val response =
                async(context = Dispatchers.IO) {
                    api.provideAntiHoro().getHoroXML()
                }.await()
            if (response.isSuccessful) {
                val list: List<String> = response.body()?.getTodayHoroList() ?: emptyList()
                databaseDao.insert(list.listHoroToEntity())

                myLogNet("list size: " + list.size.toString())
                list.forEach { myLogNet(it) }

                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(list)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    myLogNet("error noun body" + response.errorBody().toString())
                    val list = databaseDao.getHoroEntity().list
                    dataEmitter.onNext(list)
                    onSuccess()
                }
            }
        }
    }


}