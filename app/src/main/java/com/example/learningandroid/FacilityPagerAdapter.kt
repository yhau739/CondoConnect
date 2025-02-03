package com.example.learningandroid

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FacilityPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UpcomingBookingsFragment()
            1 -> HistoryBookingsFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
