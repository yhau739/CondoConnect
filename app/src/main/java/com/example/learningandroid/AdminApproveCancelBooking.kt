package com.example.learningandroid

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FacilityBookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminApproveCancelBooking : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: AdminBookingAdapter
    private lateinit var bookingList: MutableList<FacilityBookingModel>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_approve_cancel_booking)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Facilities")

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        bookingList = mutableListOf()
        bookingAdapter = AdminBookingAdapter(bookingList)
        recyclerView.adapter = bookingAdapter

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        fetchBookingData()
    }
    
    private fun fetchBookingData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                )

                for (facilitySnapshot in snapshot.children) {
                    val bookingsSnapshot = facilitySnapshot.child("bookings")
                    for (bookingSnapshot in bookingsSnapshot.children) {
                        val booking = bookingSnapshot.getValue(FacilityBookingModel::class.java)
                        if (booking != null && booking.bookingStatus != "Approved") {
                            try {
                                val bookingDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(booking.bookingDate)
                                if (bookingDate != null && !bookingDate.before(currentDate)) {
                                    booking.id = bookingSnapshot.key ?: ""
                                    bookingList.add(booking)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                bookingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

}
