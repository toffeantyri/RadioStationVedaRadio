package ru.music.radiostationvedaradio.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import ru.music.radiostationvedaradio.data.api.ApiProvider

abstract class BaseRepository<T>(val api: ApiProvider) {

    val dataEmitter: MutableStateFlow<T?> = MutableStateFlow(null)

}