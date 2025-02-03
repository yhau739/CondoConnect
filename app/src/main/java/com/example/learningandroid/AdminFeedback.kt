package com.example.learningandroid

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FeedbackModel
import com.google.firebase.database.*

class AdminFeedback : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter
    private lateinit var feedbackList: MutableList<FeedbackModel>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_feedback)

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Feedback")

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewFeedback)
        recyclerView.layoutManager = LinearLayoutManager(this)
        feedbackList = mutableListOf()
        feedbackAdapter = FeedbackAdapter(feedbackList)
        recyclerView.adapter = feedbackAdapter

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        fetchFeedbacks()
    }

    private fun fetchFeedbacks() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                feedbackList.clear()
                for (feedbackSnapshot in snapshot.children) {
                    val feedback = feedbackSnapshot.getValue(FeedbackModel::class.java)
                    if (feedback != null) {
                        feedbackList.add(feedback)
                    }
                }
                feedbackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
