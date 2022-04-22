package ru.music.radiostationvedaradio.services

import android.content.BroadcastReceiver

const val TAG_STATE_SERVICE = "StateService"
const val Broadcast_STATE_SERVICE = "ru.music.vedaradio.STATE_SERVICE"

const val TAG_NEW_AUDIO_URL = "NewAudio"
const val Broadcast_NEW_AUDIO = "ru.music.vedaradio.NEW_AUDIO"

abstract class BroadcastReceiverForPlayerService : BroadcastReceiver() {
}

