package ru.music.radiostationvedaradio.utils

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import ru.music.radiostationvedaradio.R


fun ComponentActivity.checkPermissionSingle(permission: String, onSuccess: () -> Unit) {

    val phoneStateRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            onSuccess()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    if (this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
        onSuccess()
    } else {
        phoneStateRequestPermissionLauncher.launch(permission)
    }
}

