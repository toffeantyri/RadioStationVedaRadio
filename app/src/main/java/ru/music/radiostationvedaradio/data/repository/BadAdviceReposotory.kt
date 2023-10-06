package ru.music.radiostationvedaradio.data.repository


import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.music.radiostationvedaradio.app.App
import ru.music.radiostationvedaradio.data.api.ApiProvider
import ru.music.radiostationvedaradio.data.database.room.AntiHoroscopeDao
import ru.music.radiostationvedaradio.data.model.antihoro.HoroItemHolder
import ru.music.radiostationvedaradio.utils.myLogNet
import ru.music.radiostationvedaradio.utils.toListHoroItemHolder
import ru.music.radiostationvedaradio.utils.toListSerilizeJson


class BadAdviceReposotory(api: ApiProvider) : BaseRepository<List<HoroItemHolder>>(api) {

    private val databaseDao: AntiHoroscopeDao = App.db.getRoomDao()

    //check: if database.entity.date == date -> onSuccess, else onFail
    suspend fun loadFromDatabaseAndCheckDate(
        date: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        val queryToDb = CoroutineScope(Dispatchers.IO).async {
            databaseDao.getHoroEntityByDate(date)
        }.await()


        myLogNet("date : $date == ${queryToDb?.date}")

        if (queryToDb != null) {
            if (queryToDb.date.isNotEmpty() && date == queryToDb.date) {
                withContext(Dispatchers.Main) {
                    dataEmitter.emit(queryToDb.toListHoroItemHolder())
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
                dataEmitter.emit(list)
                onSuccess()
            }
        }
        CoroutineScope(context = Dispatchers.IO).launch(exceptionHandler) {
            myLogNet("pre response")
            val response =
                async(context = Dispatchers.IO) {
                    api.provideAntiHoro().getHoroXML()
                }.await()
            myLogNet("post response")
            myLogNet("post responce: ${response.isSuccessful}")
            if (response.isSuccessful) {
                myLogNet("responce: ${response.isSuccessful}")

                val list: List<List<HoroItemHolder>> = response.body()!!.toListHoroItemHolder()

                list.forEach { myLogNet(" list toHoroItemHolder" + it[0].name) }

                databaseDao.insert(list[0].toListSerilizeJson(0))
                databaseDao.insert(list[1].toListSerilizeJson(1))
                databaseDao.insert(list[2].toListSerilizeJson(2))
                withContext(Dispatchers.Main) {
                    list[0].forEach { "dataEmitter OnNext responce" + myLogNet(it.name) }
                    dataEmitter.emit(list[0])
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    val list: List<HoroItemHolder> =
                        databaseDao.getHoroEntity()?.toListHoroItemHolder() ?: emptyList()
                    dataEmitter.emit(list)
                    onSuccess()
                }
            }
        }
    }
}