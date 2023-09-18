package ru.music.radiostationvedaradio.busines.repository

import kotlinx.coroutines.flow.MutableStateFlow
import ru.music.radiostationvedaradio.busines.api.ApiProvider

abstract class BaseRepository<T>(val api: ApiProvider) {

    val dataEmitter: MutableStateFlow<T?> = MutableStateFlow(null)

}