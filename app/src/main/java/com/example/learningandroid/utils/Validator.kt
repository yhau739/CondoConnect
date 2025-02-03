package com.example.learningandroid.utils

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

class Validator {

    companion object {
        fun validateUsername(username: String): Boolean {
            return username.isNotEmpty()
        }

        fun validateEmail(email: String): Boolean {
            return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validatePassword(password: String): Boolean {
            return password.length >= 6 // Example: Password should be at least 6 characters long
        }

        fun validatePasswordConfirmation(password: String, confirmPassword: String): Boolean {
            return password == confirmPassword
        }

        suspend fun isUsernameDuplicate(username: String, database: DatabaseReference): Boolean {
            val snapshot = database.child("Users").orderByChild("username").equalTo(username).get().await()
            return snapshot.exists()
        }

        suspend fun isEmailDuplicate(email: String, database: DatabaseReference): Boolean {
            val snapshot = database.child("Users").orderByChild("email").equalTo(email).get().await()
            return snapshot.exists()
        }
    }
}