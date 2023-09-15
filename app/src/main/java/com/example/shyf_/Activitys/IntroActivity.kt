package com.example.shyf_.Activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shyf_.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //..... code
        onClickItem()
    }

    private fun onClickItem() {
        binding.btnStart.setOnClickListener {
            val i = Intent(applicationContext, SignInActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}