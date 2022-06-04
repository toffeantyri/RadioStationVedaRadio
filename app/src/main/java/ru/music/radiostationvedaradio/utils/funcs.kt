package ru.music.radiostationvedaradio.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.busines.database.room.AntiHoroTodayEntity
import ru.music.radiostationvedaradio.busines.model.antihoro.HoroscopeModelClasses
import java.text.SimpleDateFormat
import java.util.*

//Функция достаёт нашу цитату из этого html
fun String.parceNounHareKrishnaFromHtml(): String {
    val regexpLine = "\".[^a-z]{50,1500}\"".trimMargin()
    val found = regexpLine.toRegex().find(this)
    val formattedText = found?.value?.replace(". ", ".\n\n")
    val formattedText2 = formattedText?.replace("\"", " ")

    return formattedText2 ?: ""
}

fun myLog(message: String) {
    Log.d(TAG, message)
}

fun myLogNet(message: String) {
    Log.d(TAG_NET, message)
}

fun NavController.navigateChangeTitleToolbar(
    parentAcivity: MainActivity,
    idAction: Int,
    newTitle: String = parentAcivity.getString(R.string.app_name)
) {
    this.navigate(idAction)
    parentAcivity.mToolbar.title = newTitle
}

//функция проверки запущен ли сервис Класса T
private fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }

fun HoroscopeModelClasses.getTodayHoroList(): List<String> {
    val list = arrayListOf<String>()
    list.add(0, this.date?.today ?: "")
    list.add(1, this.aries?.get(2) ?: "")
    list.add(2, this.taurus?.get(2) ?: "")
    list.add(3, this.gemini?.get(2) ?: "")
    list.add(4, this.cancer?.get(2) ?: "")
    list.add(5, this.leo?.get(2) ?: "")
    list.add(6, this.virgo?.get(2) ?: "")
    list.add(7, this.libra?.get(2) ?: "")
    list.add(8, this.scorpio?.get(2) ?: "")
    list.add(9, this.sagittarius?.get(2) ?: "")
    list.add(10, this.capricorn?.get(2) ?: "")
    list.add(11, this.aquarius?.get(2) ?: "")
    list.add(12, this.pisces?.get(2) ?: "")
    return list
}

fun List<String>.listHoroToEntity(): AntiHoroTodayEntity = AntiHoroTodayEntity(date = this[0], list = this)

fun getTodayDate(format: String): String {
    val date = Calendar.getInstance().time
    val formatter = SimpleDateFormat(format)
    return formatter.format(date)
}



