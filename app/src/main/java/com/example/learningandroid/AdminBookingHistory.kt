package com.example.learningandroid

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FacilityBookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminBookingHistory : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: MutableList<FacilityBookingModel>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_booking_history)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Facilities")

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewHistoryBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        bookingList = mutableListOf()
        bookingAdapter = BookingAdapter(bookingList)
        recyclerView.adapter = bookingAdapter

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        fetchBookingHistory()
    }

    private fun fetchBookingHistory() {

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                for (facilitySnapshot in snapshot.children) {
                    val bookingsSnapshot = facilitySnapshot.child("bookings")
                    for (bookingSnapshot in bookingsSnapshot.children) {
                        val booking = bookingSnapshot.getValue(FacilityBookingModel::class.java)
                        if (booking != null && !isFutureDate(booking.bookingDate, currentDate)) {
                            bookingList.add(booking)
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

    private fun isFutureDate(bookingDate: String, currentDate: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val bookingDateParsed = sdf.parse(bookingDate)
            val currentDateParsed = sdf.parse(currentDate)
            bookingDateParsed?.after(currentDateParsed) ?: false
        } catch (e: Exception) {
            false
        }
    }
}
