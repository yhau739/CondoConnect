package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FeedbackModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyFeedback : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter
    private lateinit var feedbackList: MutableList<FeedbackModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_feedback)

        recyclerView = findViewById(R.id.recyclerViewFeedback)
        recyclerView.layoutManager = LinearLayoutManager(this)
        feedbackList = mutableListOf()
        feedbackAdapter = FeedbackAdapter(feedbackList)
        recyclerView.adapter = feedbackAdapter

        // Initialize Firebase components
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        database = MainActivity.getDatabase().getReference("Feedback")

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // Set the selected item
        bottomNavigationView.selectedItemId = R.id.navigation_feedback
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle home navigation
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_feedback -> {
                    // Handle club navigation
                    true
                }

                R.id.navigation_profile -> {
                    // Handle profile navigation
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Fetch feedback from the database
        fetchFeedback(currentUserId)
    }

    private fun fetchFeedback(userId: String?) {
        userId?.let {
            database.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        feedbackList.clear()
                        for (feedbackSnapshot in snapshot.children) {
                            val feedback = feedbackSnapshot.getValue(FeedbackModel::class.java)
                            feedback?.let { feedbackList.add(it) }
                        }
                        feedbackAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database error
                    }
                })
        }
    }
}
