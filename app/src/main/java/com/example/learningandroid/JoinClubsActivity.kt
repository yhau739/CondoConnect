package com.example.learningandroid

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.ClubModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class JoinClubsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clubsAdapter: ClubsAdapter
    private lateinit var clubsList: MutableList<ClubModel>
    private lateinit var database: DatabaseReference
    private lateinit var userClubs: MutableList<String>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_clubs)

        recyclerView = findViewById(R.id.recyclerViewJoinClubs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        clubsList = mutableListOf()
        clubsAdapter = ClubsAdapter(clubsList)
        recyclerView.adapter = clubsAdapter

        auth = FirebaseAuth.getInstance()
        database = MainActivity.getDatabase().reference

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        fetchUserClubs()
    }

    private fun fetchUserClubs() {
        val userId = auth.currentUser?.uid ?: return
        userClubs = mutableListOf()

        database.child("Users").child(userId).child("clubs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (clubSnapshot in snapshot.children) {
                        userClubs.add(clubSnapshot.key ?: "")
                    }
                    fetchClubs()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun fetchClubs() {
        database.child("Clubs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clubsList.clear()
                for (clubSnapshot in snapshot.children) {
                    val club = clubSnapshot.getValue(ClubModel::class.java)
                    if (club != null && !userClubs.contains(club.id)) {
                        clubsList.add(club)
                    }
                }
                clubsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
