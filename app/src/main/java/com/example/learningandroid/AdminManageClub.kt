package com.example.learningandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.ClubModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class AdminManageClub : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clubAdapter: AdminClubAdapter
    private lateinit var clubList: MutableList<ClubModel>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_club)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewClubs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        clubList = mutableListOf()
        clubAdapter = AdminClubAdapter(clubList)
        recyclerView.adapter = clubAdapter

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Clubs")

        // Fetch clubs from Firebase
        fetchClubs()

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        // Set up the FloatingActionButton to add a new club
        val addClubButton = findViewById<FloatingActionButton>(R.id.addClubButton)
        addClubButton.setOnClickListener {
            val intent = Intent(this, AddClubActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchClubs() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clubList.clear()
                for (clubSnapshot in snapshot.children) {
                    val club = clubSnapshot.getValue(ClubModel::class.java)
                    if (club != null) {
                        clubList.add(club)
                    }
                }
                clubAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
