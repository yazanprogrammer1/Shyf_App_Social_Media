package com.example.shyf_.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shyf_.fragments.PostUserFragment
import com.example.shyf_.fragments.SaveUserPostFragment

class ViewPagerProfileAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        // قم بتعريف وإرجاع الـ `Fragments` التي ترغب في عرضها في كل علامة (Tab) هنا.
        return when (position) {
            0 -> PostUserFragment()
            1 -> SaveUserPostFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int {
        // قم بإرجاع عدد العلامات (Tabs) هنا.
        return 2 // لدينا علامتين فقط: "All" و "Followed".
    }
}

