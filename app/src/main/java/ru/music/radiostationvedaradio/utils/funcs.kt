package ru.music.radiostationvedaradio.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity
import ru.music.radiostationvedaradio.busines.database.room.AntiHoroTodayEntity
import ru.music.radiostationvedaradio.busines.model.antihoro.HoroItemHolder
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


fun getTodayDate(format: String): String {
    val date = Calendar.getInstance().time
    val formatter = SimpleDateFormat(format)
    return formatter.format(date)
}

fun HoroscopeModelClasses.toListHoroItemHolder(): List<List<HoroItemHolder>> {
    val allList = arrayListOf<List<HoroItemHolder>>()
    val listToday = arrayListOf<HoroItemHolder>()
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Овен", this.aries?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Телец", this.taurus?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Близнецы", this.gemini?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Рак", this.cancer?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Лев", this.leo?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Дева", this.virgo?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Весы", this.libra?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Скорпион", this.scorpio?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Стрелец", this.sagittarius?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Козерог", this.capricorn?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Водолей", this.aquarius?.get(1) ?: ""))
    listToday.add(HoroItemHolder(this.date?.today ?: "", "Рыбы", this.pisces?.get(1) ?: ""))
    val tom = arrayListOf<HoroItemHolder>()
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Овен", this.aries?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Телец", this.taurus?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Близнецы", this.gemini?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Рак", this.cancer?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Лев", this.leo?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Дева", this.virgo?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Весы", this.libra?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Скорпион", this.scorpio?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Стрелец", this.sagittarius?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Козерог", this.capricorn?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Водолей", this.aquarius?.get(2) ?: ""))
    tom.add(HoroItemHolder(this.date?.tomorrow ?: "", "Рыбы", this.pisces?.get(2) ?: ""))
    val tom2 = arrayListOf<HoroItemHolder>()
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Овен", this.aries?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Телец", this.taurus?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Близнецы", this.gemini?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Рак", this.cancer?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Лев", this.leo?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Дева", this.virgo?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Весы", this.libra?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Скорпион", this.scorpio?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Стрелец", this.sagittarius?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Козерог", this.capricorn?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Водолей", this.aquarius?.get(3) ?: ""))
    tom2.add(HoroItemHolder(this.date?.tomorrow02 ?: "", "Рыбы", this.pisces?.get(3) ?: ""))
    allList.add(listToday)
    allList.add(tom)
    allList.add(tom2)
    return allList
}

fun List<HoroItemHolder>.toListSerilizeJson(id: Int): AntiHoroTodayEntity {
    val mapper = jacksonObjectMapper()
    return AntiHoroTodayEntity(
        id = id,
        date = this[0].date,
        list = this.map { mapper.writeValueAsString(it) })
}

fun AntiHoroTodayEntity.toListHoroItemHolder(): List<HoroItemHolder> {
    val mapper = jacksonObjectMapper()
    return this.list.map { mapper.readValue(it, HoroItemHolder::class.java) }
}

