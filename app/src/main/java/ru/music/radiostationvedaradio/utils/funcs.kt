package ru.music.radiostationvedaradio.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.activityes.MainActivity

//Функция достаёт нашу цитату из этого html
fun String.parceNounHareKrishnaFromHtml(): String {
    val regexpLine = "\".[^a-z]{50,1500}\"".trimMargin()
    val found = regexpLine.toRegex().find(this)
    val formattedText = found?.value?.replace(". ", ".\n\n")
    val formattedText2 = formattedText?.replace("\"", " ")

    return formattedText2 ?: ""
}

fun MyLog(message : String){
    Log.d(TAG, message)
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
