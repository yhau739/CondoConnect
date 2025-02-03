package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FetchNoticeModel
import com.example.learningandroid.models.NoticeModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminNoticeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_notice)

        // Initialize the back arrow
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewNotices)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase Database
        database = MainActivity.getDatabase().reference.child("Notices")

        fetchNotices()

        // Set up the FAB to add new notice
        findViewById<FloatingActionButton>(R.id.fabAddNotice).setOnClickListener {
            startActivity(Intent(this, AddNoticeActivity::class.java))
        }
    }

    private fun fetchNotices() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notices = mutableListOf<FetchNoticeModel>()
                for (noticeSnapshot in dataSnapshot.children) {
                    val notice = noticeSnapshot.getValue(FetchNoticeModel::class.java)
                    if (notice != null) {
                        val noticeWithId = notice.copy(id = noticeSnapshot.key ?: "")
                        notices.add(noticeWithId)
                    }
                }
                noticeAdapter = NoticeAdapter(notices, true)
                recyclerView.adapter = noticeAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    fun refreshNotices() {
        fetchNotices()
    }
}
