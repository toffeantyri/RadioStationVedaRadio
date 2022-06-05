package ru.music.radiostationvedaradio.busines.repository


import kotlinx.coroutines.*
import ru.music.radiostationvedaradio.App
import ru.music.radiostationvedaradio.busines.api.ApiProvider
import ru.music.radiostationvedaradio.busines.database.room.AntiHoroscopeDao
import ru.music.radiostationvedaradio.busines.model.antihoro.HoroItemHolder
import ru.music.radiostationvedaradio.utils.myLog
import ru.music.radiostationvedaradio.utils.myLogNet
import ru.music.radiostationvedaradio.utils.toListHoroItemHolder
import ru.music.radiostationvedaradio.utils.toListSerilizeJson


class BadAdviceReposotory(api: ApiProvider) : BaseRepository<List<HoroItemHolder>>(api) {

    private val databaseDao: AntiHoroscopeDao = App.Companion.db.getRoomDao()

    //check: if database.entity.date == date -> onSuccess, else onFail
    suspend fun loadFromDatabaseAndCheckDate(
        date: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        val queryToDb = CoroutineScope(Dispatchers.IO).async {
            databaseDao.getHoroEntityByDate(date)
        }.await()

        if (queryToDb != null) {
            if (!queryToDb.date.isNullOrEmpty() && date == queryToDb.date) {
                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(queryToDb.toListHoroItemHolder())
                    onSuccess()
                }
            } else {
                onFail()
            }
        } else {
            onFail()
        }
    }

    /**   Load horoscope:
     *      if (no internet) -> handleException -> return list from database or empty list
     *      if (any error) ->   return list from database or empty list
     */
    fun loadNewHoro(onSuccess: () -> Unit) {

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            myLogNet("exceptionHandlerCoroutine BARepo : " + exception.message.toString())
            CoroutineScope(Dispatchers.Main).launch {
                val list: List<HoroItemHolder> =
                    databaseDao.getHoroEntity()?.toListHoroItemHolder() ?: emptyList()
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
                val list: List<List<HoroItemHolder>> = response.body()!!.toListHoroItemHolder()
                databaseDao.insert(list[0].toListSerilizeJson(0))
                databaseDao.insert(list[1].toListSerilizeJson(1))
                databaseDao.insert(list[2].toListSerilizeJson(2))
                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(list[0])
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    val list: List<HoroItemHolder> =
                        databaseDao.getHoroEntity()?.toListHoroItemHolder() ?: emptyList()
                    dataEmitter.onNext(list)
                    onSuccess()
                }
            }
        }
    }
}