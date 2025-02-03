package com.example.learningandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FacilityBookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryBookingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: MutableList<FacilityBookingModel>
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_bookings, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Facilities")

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recyclerViewHistoryBookings)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookingList = mutableListOf()
        bookingAdapter = BookingAdapter(bookingList)
        recyclerView.adapter = bookingAdapter



        fetchHistoryBookings()

        return view
    }

    private fun fetchHistoryBookings() {
        val currentUserId = auth.currentUser?.uid ?: return

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                for (facilitySnapshot in snapshot.children) {
                    val bookingsSnapshot = facilitySnapshot.child("bookings")
                    for (bookingSnapshot in bookingsSnapshot.children) {
                        val booking = bookingSnapshot.getValue(FacilityBookingModel::class.java)
                        if (booking != null && booking.uid == currentUserId && isPastDate(booking.bookingDate, currentDate)) {
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

    private fun isPastDate(bookingDate: String, currentDate: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val bookingDateParsed = sdf.parse(bookingDate)
            val currentDateParsed = sdf.parse(currentDate)
            bookingDateParsed?.before(currentDateParsed) ?: false
        } catch (e: Exception) {
            false
        }
    }
}
