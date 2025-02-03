package com.example.learningandroid

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class NoticeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)

        // Retrieve views by their IDs
        val ivNoticeDetailImage: ImageView = findViewById(R.id.ivNoticeDetailImage)
        val tvNoticeDetailTitle: TextView = findViewById(R.id.tvNoticeDetailTitle)
        val tvNoticeDetailSubtitle: TextView = findViewById(R.id.tvNoticeDetailSubtitle)
        val tvNoticeDetailDate: TextView = findViewById(R.id.tvNoticeDetailDate)
        val tvNoticeDetailDescription: TextView = findViewById(R.id.tvNoticeDetailDescription)
        val backArrow: ImageView = findViewById(R.id.backArrow)

        // Retrieve data from intent
        val title = intent.getStringExtra("NOTICE_TITLE")
        val subtitle = intent.getStringExtra("NOTICE_SUBTITLE")
        val date = intent.getStringExtra("NOTICE_DATE")
        val description = intent.getStringExtra("NOTICE_DESCRIPTION")
        val content = intent.getStringExtra("NOTICE_CONTENT")
        val imageUrl = intent.getStringExtra("NOTICE_IMAGE")

        // Set data to views
        tvNoticeDetailTitle.text = title
        tvNoticeDetailSubtitle.text = subtitle
        tvNoticeDetailDate.text = date
        tvNoticeDetailDescription.text = content

        // Use Glide to load the image from the URL
        Glide.with(this)
            .load(imageUrl)
            .into(ivNoticeDetailImage)

        // Back button functionality
        backArrow.setOnClickListener {
            finish()
        }
    }
}
