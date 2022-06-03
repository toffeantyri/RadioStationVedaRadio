package ru.music.radiostationvedaradio.busines.database

import android.content.Context
import android.content.SharedPreferences



const val PREFERENCES_NAME = "AppPreferences"
const val TEXT_NOUN = "text_noun"

object SharedPreferenceProvider {

    private lateinit var pref : SharedPreferences

    fun getSharedPreferences(context: Context) : SharedPreferences{
        pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return pref
    }

    fun saveNoun(text: String) {
        pref.edit().putString(TEXT_NOUN, text).apply()
    }

    fun loadNoun(): String {
        return pref.getString(TEXT_NOUN, "") ?: ""
    }


}