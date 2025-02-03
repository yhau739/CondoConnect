package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learningandroid.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvOwner: TextView
    private lateinit var tvUserType: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsernameEdit: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // Set the selected item
        bottomNavigationView.selectedItemId = R.id.navigation_profile
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
                    val intent = Intent(this, MyFeedback::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    // Handle profile navigation
                    true
                }
                else -> false
            }
        }

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = MainActivity.getDatabase().getReference("Users")

        // Initialize views
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvUserType = findViewById(R.id.tvUserType)
        tvUsernameEdit = findViewById(R.id.tvUsernameEdit)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)

        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        // Fetch and display user data
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        tvUsername.text = user.username ?: "N/A"
                        tvEmail.text = user.email ?: "N/A"
                        tvUserType.text = "Type: " + (if (user.owner == true) "Owner" else "Tenant")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun updateProfile() {
        val newUsername = tvUsernameEdit.text.toString().trim()
        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            val userUpdates = mapOf<String, Any>(
                "username" to newUsername
            )

            database.child(userId).updateChildren(userUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    tvUsername.text = newUsername
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
