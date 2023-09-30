package ru.music.radiostationvedaradio.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import ru.music.radiostationvedaradio.R

fun Activity.exitDialog(onFullExit: () -> Unit) {
    AlertDialog.Builder(this).apply {
        setMessage(R.string.alert_mes_exit)
            .setCancelable(true)
            .setPositiveButton(
                R.string.alert_mes_yes_all
            ) { _, _ ->
                onFullExit()
                finish()
            }
        setNegativeButton(
            R.string.alert_mes_yes
        ) { _, _ ->
            finish()
        }
        setNeutralButton(
            R.string.alert_mes_no
        ) { dialog, _ -> dialog.cancel() }
    }.create().show()
}

fun Context.openIntentUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}