package com.example.learningandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FacilityBookingModel
import java.text.SimpleDateFormat
import java.util.Locale

class BookingAdapter(private val bookingList: List<FacilityBookingModel>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.bookingTitle.text = booking.title
//        holder.bookingDate.text = booking.bookingDate
        holder.bookingDate.text = formatDate(booking.bookingDate)
        holder.bookingTime.text = "${booking.bookingStartTime} - ${booking.bookingEndTime}"
        holder.bookingStatus.text = booking.bookingStatus

        // Assuming you have a URL for the booking image, you can use Glide to load it
         Glide.with(holder.itemView.context).load(booking.imageUrl).into(holder.bookingImage)

        // Change button color and text color based on booking status
        if (booking.bookingStatus != "Approved") {
            holder.bookingStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.bookingStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            // Reset to default colors if needed
            holder.bookingStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.bookingStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.darkergreen))
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingImage: ImageView = itemView.findViewById(R.id.bookingImage)
        val bookingTitle: TextView = itemView.findViewById(R.id.bookingTitle)
        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        val bookingTime: TextView = itemView.findViewById(R.id.bookingTime)
        val bookingStatus: Button = itemView.findViewById(R.id.bookingStatus)
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val targetFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            val date = originalFormat.parse(dateStr)
            targetFormat.format(date)
        } catch (e: Exception) {
            dateStr // Return original date string in case of an error
        }
    }
}
