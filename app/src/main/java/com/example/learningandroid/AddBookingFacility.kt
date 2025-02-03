package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class AddBookingFacility : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var facilityAdapter: AddFacilityAdapter
    private lateinit var facilityList: MutableList<FacilityModel>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_booking_facility)

        // Initialize data
        facilityList = mutableListOf()

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Facilities")

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFacilities)
        recyclerView.layoutManager = LinearLayoutManager(this)
        facilityAdapter = AddFacilityAdapter(facilityList) { facility ->
            // Handle facility item click
//            Toast.makeText(this, "Add Facility ${facility.title}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AddBookingPage::class.java)
            intent.putExtra("facilityId", facility.id)
            intent.putExtra("facilityTitle", facility.title)
            intent.putExtra("facilityImageUrl", facility.imageUrl)
            startActivity(intent)
        }
        recyclerView.adapter = facilityAdapter

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        fetchFacilities()
    }

    private fun fetchFacilities() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                facilityList.clear()
                for (dataSnapshot in snapshot.children) {
                    val facility = dataSnapshot.getValue(FacilityModel::class.java)
//                    if (facility != null) {
//                        facilityList.add(facility)
//                    }
                    facility?.let {
                        it.id = dataSnapshot.key ?: ""
                        facilityList.add(it)
                    }
                }
                facilityAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}

