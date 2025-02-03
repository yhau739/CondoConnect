package com.example.learningandroid

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.learningandroid.models.User
import com.example.learningandroid.models.isOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgetpassButton: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // get database instance
        database = MainActivity.getDatabase().reference
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // get value from ui text inputs
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgetpassButton = findViewById(R.id.forgetButton)

        // onclick for login button
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            Log.d("LoginActivity", "Login button clicked with username: $username")

            if (username.isNotEmpty() && password.isNotEmpty()) {
//                login(username, password)
                signIn(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // onclick for forget password button
        forgetpassButton.setOnClickListener {
            val intent = Intent(this@Login, ForgotPassword::class.java)
            startActivity(intent)
        }

    }


    fun signIn(identifier: String, password: String) {
        if (identifier.contains("@")) {
            // Assume it's an email
            signInWithEmail(identifier, password)
        } else {
            // Its not a valid email format
            Toast.makeText(baseContext, "Invalid Email Format", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    checkIfAdmin(user?.uid) { isAdmin ->
                        if (isAdmin) {
                            Toast.makeText(
                                baseContext,
                                "Admin Login successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this, AdminHome::class.java))
                        }else {
                            Toast.makeText(baseContext, "User logged in: ${user?.email}", Toast.LENGTH_SHORT).show()
                            checkIfOwner(user)
                        }
                    }
                } else {
                    Toast.makeText(baseContext, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkIfOwner(user: FirebaseUser?) {
        if (user != null) {
            isOwner(user.uid) { isOwner ->
                startActivity(Intent(this, Home::class.java))
            }
        }
    }

    private fun checkIfAdmin(userId: String?, callback: (Boolean) -> Unit) {
        if (userId == null) {
            callback(false)
            return
        }
        database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isAdmin = snapshot.child("admin").getValue(Boolean::class.java) ?: false
                callback(isAdmin)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("LoginActivity", "checkIfAdmin:onCancelled", error.toException())
                callback(false)
            }
        })
    }
}