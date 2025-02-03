package com.example.learningandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FacilityBookingModel
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Locale

class AdminBookingAdapter(private val bookingList: List<FacilityBookingModel>) :
    RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder>() {

    private val database: DatabaseReference = MainActivity.getDatabase().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_booking, parent, false)
        return AdminBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminBookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.bookingTitle.text = booking.title
        holder.bookingDate.text = formatDate(booking.bookingDate)
        holder.bookingTime.text = "${booking.bookingStartTime} - ${booking.bookingEndTime}"

        // Assuming you have a URL for the booking image, you can use Glide to load it
        Glide.with(holder.itemView.context).load(booking.imageUrl).into(holder.bookingImage)

        // Handle approve and cancel actions
        holder.approveButton.setOnClickListener {
            // Implement approve action
            approveBooking(holder.itemView.context, booking.id)
        }

        holder.cancelButton.setOnClickListener {
            // Implement cancel action
            cancelBooking(holder.itemView.context,booking.id)
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    class AdminBookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingImage: ImageView = itemView.findViewById(R.id.bookingImage)
        val bookingTitle: TextView = itemView.findViewById(R.id.bookingTitle)
        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        val bookingTime: TextView = itemView.findViewById(R.id.bookingTime)
        val approveButton: Button = itemView.findViewById(R.id.approve_btn)
        val cancelButton: Button = itemView.findViewById(R.id.cancel_btn)
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val targetFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            val date = originalFormat.parse(dateStr)
            targetFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun approveBooking(context: Context, bookingId: String) {
        val facilitiesRef = database.child("Facilities")
        facilitiesRef.get().addOnSuccessListener { snapshot ->
            var bookingFound = false
            for (facilitySnapshot in snapshot.children) {
                val bookingsSnapshot = facilitySnapshot.child("bookings")
                for (bookingSnapshot in bookingsSnapshot.children) {
                    if (bookingSnapshot.key == bookingId) {
                        bookingSnapshot.ref.child("bookingStatus").setValue("Approved").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Update the booking status in the list and notify the adapter
                                Toast.makeText(context, "Booking approved successfully", Toast.LENGTH_SHORT).show()
                                if (context is Activity) {
                                    context.finish()
                                }
                                context.startActivity(Intent(context, AdminApproveCancelBooking::class.java))
                            } else {
                                // Handle failure
                                Toast.makeText(context, "Failed to approve booking", Toast.LENGTH_SHORT).show()
                            }
                        }
                        bookingFound = true
                        break
                    }
                }
                if (bookingFound) break
            }
        }
    }

    private fun cancelBooking(context: Context, bookingId: String) {
        val facilitiesRef = database.child("Facilities")
        facilitiesRef.get().addOnSuccessListener { snapshot ->
            var bookingFound = false
            for (facilitySnapshot in snapshot.children) {
                val bookingsSnapshot = facilitySnapshot.child("bookings")
                for (bookingSnapshot in bookingsSnapshot.children) {
                    if (bookingSnapshot.key == bookingId) {
                        bookingSnapshot.ref.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Remove the booking from the list and notify the adapter
                                Toast.makeText(context, "Booking cancelled successfully", Toast.LENGTH_SHORT).show()
                                if (context is Activity) {
                                    context.finish()
                                }
                                context.startActivity(Intent(context, AdminApproveCancelBooking::class.java))
                            } else {
                                // Handle failure
                                Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show()
                            }
                        }
                        bookingFound = true
                        break
                    }
                }
                if (bookingFound) break
            }
        }
    }
}
