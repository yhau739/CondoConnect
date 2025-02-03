package com.example.learningandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.learningandroid.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class ForgotPassword : AppCompatActivity() {
    private lateinit var backArrow: ImageView
    private lateinit var resetBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // get database instance
        database = MainActivity.getDatabase().reference

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find the ImageView by its ID
        backArrow= findViewById(R.id.backArrow)
        resetBtn = findViewById(R.id.resetButton)
        emailEditText = findViewById(R.id.emailEditText)

        // Set an OnClickListener on the ImageView
        backArrow.setOnClickListener {
            // Finish the current activity to go back to the previous one
            finish()
        }

        resetBtn.setOnClickListener{
            // search firebase realtime db then check if an email exist
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                checkEmailExists(email)
            } else {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "This email is not registered", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun checkEmailExists(email: String) {
        // Query the database to search for the email
        //.equalTo(email)
        database.child("Users").orderByChild("email")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null && user.email == email) {
                                // Email exists in the database
                                Toast.makeText(this@ForgotPassword, "Email exists: ${user.email}", Toast.LENGTH_SHORT).show()

                                // send an email to user's email
                                Log.d("Forgotpass", "forgot email for email: $email")
                                sendPasswordResetEmail(user.email)

                                // Store the email in SharedPreferences
                                val sharedPref = getSharedPreferences("my.PREFERENCE_FILE_KEY", MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("reset_email", email)
                                    apply()
                                }

                                // Go to reset pass page
//                                val intent = Intent(this@ForgotPassword, ResetPassword::class.java)
//                                startActivity(intent)
                                return
                            }
                        }
                        Toast.makeText(this@ForgotPassword, "This email is not registered", Toast.LENGTH_SHORT).show()
                    } else {
                        // Email does not exist
                        Toast.makeText(this@ForgotPassword, "Email does not exist 2", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ForgotPassword, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}