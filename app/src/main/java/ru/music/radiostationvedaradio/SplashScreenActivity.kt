package ru.music.radiostationvedaradio

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash_screen.*
import ru.music.radiostationvedaradio.view.activities.BaseMainActivity
import ru.music.radiostationvedaradio.view.activities.MainActivity

class SplashScreenActivity : BaseMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val ab = supportActionBar
        ab?.hide()
        logo_splash.apply {
            scaleX = 0f
            scaleY = 0f
        }
        logo_splash.animate().setDuration(1000).scaleX(1f).scaleY(1f).withEndAction {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()


        }
    }
}
