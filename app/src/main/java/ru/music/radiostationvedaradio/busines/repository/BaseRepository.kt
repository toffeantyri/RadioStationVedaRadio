package ru.music.radiostationvedaradio.busines.repository

import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.music.radiostationvedaradio.busines.ApiProvider

abstract class BaseRepository<T>(val api : ApiProvider) {

    val dataEmitter: BehaviorSubject<T> = BehaviorSubject.create()

}