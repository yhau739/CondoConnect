package com.example.learningandroid

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ResetPassword : AppCompatActivity() {
    private lateinit var backArrow: ImageView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var resetPasswordButton: Button
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // get database instance
        database = MainActivity.getDatabase().reference

        backArrow = findViewById(R.id.backArrow)
        resetPasswordButton = findViewById(R.id.resetButton)
        newPasswordEditText = findViewById(R.id.newPassword)
        confirmPasswordEditText = findViewById(R.id.confirmNewPassword)
        sharedPref = getSharedPreferences("my.PREFERENCE_FILE_KEY", MODE_PRIVATE)

        // Retrieve the email from SharedPreferences
        val email = sharedPref.getString("reset_email", null)
        if (email == null) {
            Toast.makeText(this, "Error: No email found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up the reset password button click listener
        resetPasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    resetPassword(email, newPassword)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter and confirm your new password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set an OnClickListener on the ImageView
        backArrow.setOnClickListener {
            // Finish the current activity to go back to the previous one
            finish()
        }
    }

    private fun resetPassword(email: String, newPassword: String) {
        // Query the database to find the user by email and update the password
        database.child("Users").orderByChild("email").equalTo(email)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userKey = userSnapshot.key
                            if (userKey != null) {
                                database.child("Users").child(userKey).child("password").setValue(newPassword)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@ResetPassword, "Password reset successfully", Toast.LENGTH_SHORT).show()
                                        // Optionally, navigate back to the login screen
                                        val intent = Intent(this@ResetPassword, Login::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@ResetPassword, "Failed to reset password", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(this@ResetPassword, "Error: User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ResetPassword, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}