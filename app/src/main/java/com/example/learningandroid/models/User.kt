package com.example.learningandroid.models

import com.example.learningandroid.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

data class User(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val owner: Boolean? = null,
    val admin: Boolean? = null
)

fun isOwner(userID: String, callback: (Boolean) -> Unit) {
    val database = MainActivity.getDatabase().reference

    database.child("Users").child(userID).child("owner").addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val isOwner = snapshot.getValue(Boolean::class.java) ?: false
                callback(isOwner)
            } else {
                callback(false)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            callback(false)
        }
    })
}


//fun createAccount(username: String, email: String, password: String, owner: Boolean) {
//    auth.createUserWithEmailAndPassword(email, password)
//        .addOnCompleteListener(this) { task ->
//            if (task.isSuccessful) {
//                val user = auth.currentUser
//                user?.let {
//                    saveUserToDatabase(it, username, owner)
//                }
//            } else {
//                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
//            }
//        }
//}

//fun saveUserToDatabase(user: FirebaseUser, username: String, owner: Boolean) {
//    val database = FirebaseDatabase.getInstance().reference
//    val userId = user.uid
//    val userMap = mapOf(
//        "username" to username,
//        "email" to user.email,
//        "owner" to owner
//    )
//    database.child("Users").child(userId).setValue(userMap)
//}

//    private fun fetchEmailByUsernameAndSignIn(username: String, password: String) {
//        database.child("Users").orderByChild("username")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(userSnapshot: DataSnapshot) {
//                    if (userSnapshot.exists()) {
//                        for (user in userSnapshot.children) {
//                            val loginUser = user.getValue(User::class.java)
//                            if (loginUser != null && loginUser.username == username) {
//                                signInWithEmail(loginUser.email!!, password)
//                                return
//                            }
//                        }
//                    }
//
//                    // No match found
//                    Toast.makeText(this@Login, "Invalid username or password", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Toast.makeText(this@Login, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                }
//        })
//    }

//    fun syncUserWithDatabase(user: FirebaseUser) {
//        val database = FirebaseDatabase.getInstance().reference
//        val userId = user.uid
//
//        database.child("Users").child(userId).get().addOnSuccessListener { dataSnapshot ->
//            if (!dataSnapshot.exists()) {
//                val username = user.displayName ?: ""
//                val owner = false // Default value, modify as necessary
//                saveUserToDatabase(user, username, owner)
//            }
//        }.addOnFailureListener {
//            // Handle any errors
//            Toast.makeText(this, "Database error: ${it.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun saveUserToDatabase(user: FirebaseUser, username: String, owner: Boolean) {
//        val database = FirebaseDatabase.getInstance().reference
//        val userId = user.uid
//        val userMap = mapOf(
//            "username" to username,
//            "email" to user.email,
//            "owner" to owner
//        )
//        database.child("Users").child(userId).setValue(userMap)
//    }


// Login function
//    private fun login(username: String, password: String) {
//        //.equalTo(username)
//        database.child("Users").orderByChild("username")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    Log.d("LoginActivity", "DataSnapshot exists: ${dataSnapshot.exists()}")
//                    if (dataSnapshot.exists()) {
//                        for (userSnapshot in dataSnapshot.children) {
//                            val user = userSnapshot.getValue(User::class.java)
//                            Log.d("LoginActivity", "User found: ${user?.username}")
//
//
//                            if (user != null && user.password == password && user.username == username) {
//                                handleLoginSuccess(user, "user")
//                                return
//                            }
//                        }
//                    }
//
//                    //.equalTo(username)
//                    database.child("admin").orderByChild("username")
//                        .addValueEventListener(object : ValueEventListener {
//                            override fun onDataChange(adminSnapshot: DataSnapshot) {
//                                if (adminSnapshot.exists()) {
//                                    for (admin in adminSnapshot.children) {
//                                        val adminUser = admin.getValue(User::class.java)
//                                        if (adminUser != null && adminUser.password == password && adminUser.username == username) {
//                                            handleLoginSuccess(adminUser, "admin")
//                                            return
//                                        }
//                                    }
//                                }
//
//                                // No match found
//                                Toast.makeText(this@Login, "Invalid username or password", Toast.LENGTH_SHORT).show()
//                            }
//
//                            override fun onCancelled(databaseError: DatabaseError) {
//                                Toast.makeText(this@Login, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Toast.makeText(this@Login, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun handleLoginSuccess(user: User, userType: String) {
//        // Handle successful login based on user type
//        val message = when (userType) {
//            "admin" -> "Welcome, Admin ${user.username}!"
//            "user" -> {
//                if (user.owner == true) "Welcome, Owner ${user.username}!" else "Welcome, ${user.username}!"
//            }
//            else -> "Welcome!"
//        }
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        // Navigate to another activity or perform other actions
//    }