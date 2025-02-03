package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Facility : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facility)

        // Initialize views
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        val fabAddNewBooking: FloatingActionButton = findViewById(R.id.fabAddNewBooking)

        // Set up ViewPager with the adapter
        viewPager.adapter = FacilityPagerAdapter(this)

        // Attach the ViewPager to the TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Upcoming"
                1 -> "History"
                else -> null
            }
        }.attach()

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        // Handle Floating Action Button click
        fabAddNewBooking.setOnClickListener {
            onFabAddNewBookingClick(it)
        }
    }

    private fun onFabAddNewBookingClick(view: View) {
        // Handle the click event for the Floating Action Button
        // For example, you can start a new activity to add a booking
        startActivity(Intent(this, AddBookingFacility::class.java))
    }
}
