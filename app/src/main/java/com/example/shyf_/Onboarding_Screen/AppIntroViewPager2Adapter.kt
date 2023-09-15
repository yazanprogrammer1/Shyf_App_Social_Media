package com.example.shyf_.Onboarding_Screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shyf_.Onboarding_Screen.Intro_Onboarding_ScreenActivity.Companion.MAX_STEP
import com.example.shyf_.R
import com.example.shyf_.databinding.OnboardingScreenItemBinding

class AppIntroViewPager2Adapter : RecyclerView.Adapter<PagerVH2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH2 {
        return PagerVH2(
            OnboardingScreenItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    //get the size of color array
    override fun getItemCount(): Int = MAX_STEP // Int.MAX_VALUE

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH2, position: Int) = holder.itemView.run {

        with(holder) {
            if (position == 0) {
//                context.getString(R.string.intro_title_1)
                bindingDesign.introTitle.text = "You can post your meals," +
                        " get likes from your followers, and sell your meals"
                bindingDesign.introDescription.text = "Insert Posts"
                bindingDesign.introImage.setImageResource(R.drawable.add_post_introsh)
            }
            if (position == 1) {
                bindingDesign.introTitle.text =
                    "You can order any meal you like from the chef whose meals you like"
                bindingDesign.introDescription.text = "Order a meal"
                bindingDesign.introImage.setImageResource(R.drawable.add_food_introsh)
            }
            if (position == 2) {
                bindingDesign.introTitle.text = "You can message the chef you want and your friends"
                bindingDesign.introDescription.text = "Messaging"
                bindingDesign.introImage.setImageResource(R.drawable.chat_introsh)
            }
        }
    }
}