package ru.music.radiostationvedaradio.utils


import android.annotation.SuppressLint
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.busines.repository.BaseRepository
import ru.music.radiostationvedaradio.busines.repository.MainFragmentRepository
import ru.music.radiostationvedaradio.busines.repository.MainRepository

const val TAG = "MyLog"
const val TAG_NET = "MyLogNet"


@SuppressLint("StaticFieldLeak")
internal var APP_CONTEXT : MainActivity? = null
