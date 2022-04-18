package ru.music.radiostationvedaradio

data class Source(
    val audio_info: String,
    val bitrate: Int,
    val channels: Int,
    val dummy: Any,
    val genre: String,
    val ice_bitrate: Int,
    //val ice-channels: Int,
    //val ice-samplerate: Int,
    val listener_peak: Int,
    val listeners: Int,
    val listenurl: String,
    val samplerate: Int,
    val server_description: String,
    val server_name: String,
    val server_type: String,
    val server_url: String,
    val stream_start: String,
    val stream_start_iso8601: String,
    val title: String
)