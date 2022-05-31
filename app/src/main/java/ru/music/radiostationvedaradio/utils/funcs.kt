package ru.music.radiostationvedaradio.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
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