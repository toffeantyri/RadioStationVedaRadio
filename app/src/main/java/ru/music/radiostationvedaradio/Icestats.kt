package ru.music.radiostationvedaradio

data class Icestats(
    val admin: String,
    val host: String,
    val location: String,
    val server_id: String,
    val server_start: String,
    val server_start_iso8601: String,
    val source: List<Source>
)