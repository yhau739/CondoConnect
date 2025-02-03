package com.example.learningandroid

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.learningandroid.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.example.learningandroid.utils.Validator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RegisterUser : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var backArrow: ImageView
    private lateinit var registerButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        // get database instance
        database = MainActivity.getDatabase().reference
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        // Find views by ID
        backArrow = findViewById(R.id.backArrow)
        registerButton = findViewById(R.id.registerButton)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)

        // Set OnClickListener for back arrow
        backArrow.setOnClickListener {
            finish() // Finish the current activity to go back to the previous one
        }

        // Set OnClickListener for register button
        registerButton.setOnClickListener {
            registerTenant()
        }
    }

    private fun registerTenant() {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (!Validator.validateUsername(username)) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Validator.validateEmail(email)) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Validator.validatePassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Validator.validatePasswordConfirmation(password, confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

//        val ownerID = sharedPreferences.getString("current_user_id", "")
        val ownerID = "ownerID"
        if (ownerID.isNullOrEmpty()) {
            Toast.makeText(this, "Owner ID is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        checkDuplicateUsername(username) { isUsernameDuplicate ->
            if (isUsernameDuplicate) {
                Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show()
                return@checkDuplicateUsername
            }

            checkDuplicateEmail(email) { isEmailDuplicate ->
                if (isEmailDuplicate) {
                    Toast.makeText(this, "Email already exists.", Toast.LENGTH_SHORT).show()
                    return@checkDuplicateEmail
                }

                createTenant(username, email, password, ownerID)
            }
        }
//        createTenant(username, email, password, ownerID)
    }

    private fun checkDuplicateUsername(username: String, callback: (Boolean) -> Unit) {
        database.child("Users").orderByChild("username")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)

                            if (user != null) {
                                if(user.username == username){
                                    callback(snapshot.exists())
                                }
                            }
                        }
                    }
                    callback(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    private fun checkDuplicateEmail(email: String, callback: (Boolean) -> Unit) {
        database.child("Users").orderByChild("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)

                            if (user != null) {
                                if(user.email == email){
                                    callback(snapshot.exists())
                                }
                            }
                        }
                    }
                    callback(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    fun createTenant(username: String, email: String, password: String, ownerID: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        saveTenantToDatabase(it, username, ownerID)
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun saveTenantToDatabase(user: FirebaseUser, username: String, ownerID: String) {
        val userId = user.uid
        val userMap = mapOf(
            "username" to username,
            "email" to user.email,
            "owner" to false
        )
        database.child("Users").child(userId).setValue(userMap)
        database.child("Tenants").child(userId).setValue(mapOf("userID" to userId, "ownerID" to ownerID))
        Toast.makeText(this, "New Tenant is created.", Toast.LENGTH_SHORT).show()
    }

}