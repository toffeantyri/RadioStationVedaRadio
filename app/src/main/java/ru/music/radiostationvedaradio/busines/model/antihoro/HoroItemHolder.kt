package ru.music.radiostationvedaradio.busines.model.antihoro

import java.io.Serializable

data class HoroItemHolder(
    val date: String,
    val name: String,
    val description: String
) : Serializable
