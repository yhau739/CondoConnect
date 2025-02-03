package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FeedbackModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FeedbackDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var feedbackImage: ImageView
    private lateinit var feedbackTitle: TextView
    private lateinit var feedbackType: TextView
    private lateinit var feedbackContent: TextView
    private lateinit var feedbackBy: TextView
    private lateinit var btnResolved: Button
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_detail)

        // Initialize views
        feedbackImage = findViewById(R.id.ivFeedbackDetailImage)
        feedbackTitle = findViewById(R.id.tvFeedbackDetailTitle)
        feedbackType = findViewById(R.id.tvFeedbackDetailType)
        feedbackContent = findViewById(R.id.tvFeedbackDetailContent)
        feedbackBy = findViewById(R.id.tvFeedbackBy)
        btnResolved = findViewById(R.id.btnResolved)

        // Get the feedback ID from the intent
        val feedbackId = intent.getStringExtra("FEEDBACK_ID") ?: return

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Feedback").child(feedbackId)
        usersRef = MainActivity.getDatabase().getReference("Users")


        // Fetch feedback details from Firebase
        fetchFeedbackDetails()

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }
        // Handle resolved button click
//        btnResolved.setOnClickListener {
//            markAsResolved(feedbackId)
//        }

        // Check if the current user is an admin
        checkIfUserIsAdmin {
            if (it) {
                btnResolved.setOnClickListener {
                    markAsResolved(feedbackId)
                }
            } else {
                btnResolved.visibility = View.GONE
            }
        }

    }

    private fun fetchFeedbackDetails() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedback = snapshot.getValue(FeedbackModel::class.java)
                if (feedback != null) {
                    feedbackTitle.text = feedback.title
                    feedbackType.text = feedback.type
                    feedbackContent.text = feedback.content
                    Glide.with(this@FeedbackDetailActivity).load(feedback.imageUrl).into(feedbackImage)
                    fetchUsername(feedback.userId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchUsername(userId: String) {
        val userRef = MainActivity.getDatabase().getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                feedbackBy.text = "Feedback by: ${username ?: "Unknown"}" // Set the username in the TextView
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun markAsResolved(feedbackId: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedback = snapshot.getValue(FeedbackModel::class.java)
                if (feedback != null && feedback.status == "resolved") {
                    Toast.makeText(this@FeedbackDetailActivity, "Feedback is already resolved", Toast.LENGTH_SHORT).show()
                } else {
                    feedbackId.let {
                        database.child("status").setValue("resolved").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                btnResolved.isEnabled = false
                                btnResolved.text = "Resolved"
                                Toast.makeText(this@FeedbackDetailActivity, "Feedback marked as resolved", Toast.LENGTH_SHORT).show()

                                // Redirect to another page
                                val intent = Intent(this@FeedbackDetailActivity, AdminFeedback::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                // Handle error
                                Toast.makeText(this@FeedbackDetailActivity, "Failed to mark as resolved", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@FeedbackDetailActivity, "Failed to check status", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun checkIfUserIsAdmin(callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        // Get the user ID
        val userId = currentUser?.uid
        val userRef = userId?.let { usersRef.child(it) }

        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if the user has the 'admin' attribute set to true
                    val isAdmin = snapshot.child("admin").getValue(Boolean::class.java) ?: false
                    callback(isAdmin)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    callback(false)
                }
            })
        }

    }

}
