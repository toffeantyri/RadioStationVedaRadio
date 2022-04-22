package ru.music.radiostationvedaradio.services

const val TAG_FIRST_RUN = "first_run"

enum class InitStatusMediaPlayer {
    IDLE,
    INITIALISATION,
    INIT_COMPLETE,
    PLAYING
}