package com.example.shyf_.Activitys

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.shyf_.Onboarding_Screen.Intro_Onboarding_ScreenActivity
import com.example.shyf_.R

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            val shared = getSharedPreferences("user_data", MODE_PRIVATE)
            val com = shared.getBoolean("isSign", false)
            if (com == true) {
                sharedPreferences = getSharedPreferences(
                    "user_data", MODE_PRIVATE
                )
                var isNightMode = sharedPreferences.getBoolean("NightMode", false)
                if (isNightMode) {
                    // قالب الوضع الليلي
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else if (!isNightMode) {
                    // قالب الوضع النهاري
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                val i = Intent(applicationContext, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            } else {
                val i = Intent(applicationContext, Intro_Onboarding_ScreenActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
        }, 3000)
    }
}