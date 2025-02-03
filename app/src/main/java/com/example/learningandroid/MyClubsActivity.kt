package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.ClubModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyClubsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clubsAdapter: MyClubsAdapter
    private lateinit var clubsList: MutableList<ClubModel>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_clubs)

        recyclerView = findViewById(R.id.recyclerViewMyClubs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        clubsList = mutableListOf()
        clubsAdapter = MyClubsAdapter(clubsList)
        recyclerView.adapter = clubsAdapter

        database = MainActivity.getDatabase().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: return)
            .child("clubs")

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        fetchMyClubs()
    }

    private fun fetchMyClubs() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clubsList.clear()
                for (clubSnapshot in snapshot.children) {
                    val clubId = clubSnapshot.key ?: continue
                    fetchClubDetails(clubId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun fetchClubDetails(clubId: String) {
        val clubRef = MainActivity.getDatabase().getReference("Clubs").child(clubId)
        clubRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val club = snapshot.getValue(ClubModel::class.java)
                if (club != null) {
                    clubsList.add(club)
                    clubsAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
