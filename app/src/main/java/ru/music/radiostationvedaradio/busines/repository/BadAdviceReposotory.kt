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

    //check: if database.entity.date == date -> onSuccess, else onFail
    suspend fun loadFromDatabaseAndCheckDate(date: String, onSuccess: () -> Unit, onFail: () -> Unit) {
        val queryToDb = CoroutineScope(Dispatchers.IO).async {
            databaseDao.getHoroEntity()
        }.await()

        if (queryToDb != null) {
            myLogNet(
                "send date: $date" +
                        "db date: ${queryToDb.date}"
            )
            if (!queryToDb.date.isNullOrEmpty() && date == queryToDb.date) {
                myLogNet("BAREPO loadAndCheckDate : date == date")
                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(queryToDb.list)
                    onSuccess()
                }
            } else {
                myLogNet("BAREPO loadAndCheckDate : date != date")
                onFail()
            }
        } else {
            myLogNet("BAREPO loadAndCheckDate : query = $queryToDb")
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
                val list: List<String> = databaseDao.getHoroEntity()?.list ?: emptyList()
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
                myLogNet("----------REPO : list size: " + list.size.toString())
                withContext(Dispatchers.Main) {
                    dataEmitter.onNext(list)
                    onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    myLogNet("REPO : Error XML body" + response.errorBody().toString())
                    val list: List<String> = databaseDao.getHoroEntity()?.list ?: emptyList()
                    dataEmitter.onNext(list)
                    onSuccess()
                }
            }
        }
    }
}