package com.example.shyf_.Onboarding_Screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.shyf_.Activitys.IntroActivity
import com.example.shyf_.R
import com.example.shyf_.databinding.ActivityIntro2Binding
import com.google.android.material.tabs.TabLayoutMediator

class Intro_Onboarding_ScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivityIntro2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntro2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        //...
        //............................................................
        binding.viewPager2.adapter = AppIntroViewPager2Adapter()

        //............................................................
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
        }.attach()

        //............................................................
        // https://developer.android.com/training/animation/screen-slide-2
        binding.viewPager2.setPageTransformer(ZoomOutPageTransformer())

        //............................................................
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // static indicator demp
                // bottomProgressDots(position) // indicator progress dots color change

                if (position == MAX_STEP - 1) {
                    binding.btnNext.text = getString(R.string.get_started)//"Get Started"
                    binding.btnNext.contentDescription =
                        getString(R.string.get_started)//"Get Started"
                } else {
                    binding.btnNext.text = getString(R.string.next)//"Next"
                    binding.btnNext.contentDescription = getString(R.string.next)//"Next"
                }
            }
        })

        //............................................................
        binding.btnSkip.setOnClickListener {
            val i = Intent(applicationContext, IntroActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }
        //............................................................
        binding.btnNext.setOnClickListener {
            if (binding.btnNext.text.toString() == getString(R.string.get_started)) {
                val i = Intent(applicationContext, IntroActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            } else {
                // to change current page - on click "Next BUTTON"
                val current = (binding.viewPager2.currentItem) + 1
                binding.viewPager2.currentItem = current

                // to update button text
                if (current >= MAX_STEP - 1) {
                    binding.btnNext.text = getString(R.string.get_started)//"Get Started"
                    binding.btnNext.contentDescription =
                        getString(R.string.get_started)//"Get Started"

                } else {
                    binding.btnNext.text = getString(R.string.next)//"Next"
                    binding.btnNext.contentDescription = getString(R.string.next)//"Next"
                }
            }
        }
    }

    companion object {
        const val MAX_STEP = 3
    }
}