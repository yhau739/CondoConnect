package com.example.learningandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.learningandroid.models.Bill
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class billing : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var billingPagerAdapter: BillingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        billingPagerAdapter = BillingPagerAdapter(this)
        viewPager.adapter = billingPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Outstanding"
                1 -> "Transaction History"
                else -> null
            }
        }.attach()
    }
}