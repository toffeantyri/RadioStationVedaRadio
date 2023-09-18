package ru.music.radiostationvedaradio.activityes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import ru.music.radiostationvedaradio.R

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val ab = supportActionBar
        ab?.hide()
        val logoSplash = findViewById<View>(R.id.logo_splash)
        logoSplash.apply {
            scaleX = 0f
            scaleY = 0f
        }
        logoSplash.animate().setDuration(1500).scaleX(1.3f).scaleY(1.3f).withEndAction {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
