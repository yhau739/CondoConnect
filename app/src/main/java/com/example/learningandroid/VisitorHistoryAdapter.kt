package com.example.learningandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.VisitorModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VisitorHistoryAdapter(
    private val visitorList: List<VisitorModel>,
    private val listener: OnVisitorClickListener?,
    private val isAdmin: Boolean ) :
    RecyclerView.Adapter<VisitorHistoryAdapter.VisitorViewHolder>() {

    interface OnVisitorClickListener {
        fun onVisitorClick(visitor: VisitorModel)
    }

    class VisitorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val visitorName: TextView = itemView.findViewById(R.id.tvVisitorName)
        val visitorCode: TextView = itemView.findViewById(R.id.tvVisitorCode)
        val visitorStatus: Button = itemView.findViewById(R.id.tvVisitorStatus)
        val vehicleNo: TextView = itemView.findViewById(R.id.tvVehicleNo)
        val visitorButtonType: Button = itemView.findViewById(R.id.btnVisitorType)
        val visitorCheckInTime: TextView = itemView.findViewById(R.id.tvVisitorCheckInTime)
        val visitorCheckOutTime: TextView = itemView.findViewById(R.id.tvVisitorCheckOutTime)
        val spaceTime: View = itemView.findViewById(R.id.spaceTime)
        val addMarginLayout: LinearLayout = itemView.findViewById(R.id.addMargin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visitor_history, parent, false)
        return VisitorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VisitorViewHolder, position: Int) {
        val visitor = visitorList[position]
        holder.visitorName.text = visitor.name
        holder.visitorCode.text = "Code: ${visitor.visitorCode}"
        holder.visitorStatus.text = visitor.status

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        val validTillDate = dateFormat.parse(visitor.validTill)

        if (visitor.status == "Expired" || (validTillDate != null && validTillDate.before(currentDate))){
            holder.visitorStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.visitorStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.visitorStatus.text = "Expired"
        } else {
            // Reset to default colors if needed
            holder.visitorStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.visitorStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.darkergreen))
        }

        holder.vehicleNo.text = visitor.vehicleNo

        // Set button text based on isAdmin flag
        if (isAdmin) {
            holder.visitorButtonType.text = "Check In/Out"
        } else {
            holder.visitorButtonType.text = "Regular Visitor"
        }

//        holder.visitorCheckInTime.text = if (visitor.checkInTime.isNullOrEmpty()) {
//            "In: N/A"
//        } else {
//            "In: ${visitor.checkInTime}"
//        }
//
//        holder.visitorCheckOutTime.text = if (visitor.checkOutTime.isNullOrEmpty()) {
//            "Out: N/A"
//        } else {
//            "Out: ${visitor.checkOutTime}"
//        }
        val checkInText = if (visitor.checkInTime.isNullOrEmpty()) {
            "In: N/A"
        } else {
            "In: ${visitor.checkInTime}"
        }
        holder.visitorCheckInTime.text = checkInText

        val checkOutText = if (visitor.checkOutTime.isNullOrEmpty()) {
            "Out: N/A"
        } else {
            "Out: ${visitor.checkOutTime}"
        }
        holder.visitorCheckOutTime.text = checkOutText

        // Show or hide the spaceTime view based on checkInTime and checkOutTime
        if (checkInText == "In: N/A" && checkOutText == "Out: N/A") {
            holder.spaceTime.visibility = View.GONE
            // Add left margin of 20dp to addMarginLayout
            val layoutParams = holder.addMarginLayout.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.leftMargin = (20 * holder.itemView.resources.displayMetrics.density).toInt()
            holder.addMarginLayout.layoutParams = layoutParams
        } else {
            holder.spaceTime.visibility = View.VISIBLE
            // Remove left margin if spaceTime is visible
            val layoutParams = holder.addMarginLayout.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.leftMargin = 0
            holder.addMarginLayout.layoutParams = layoutParams
        }

        holder.visitorButtonType.setOnClickListener {
            listener?.onVisitorClick(visitor)
        }
    }

    override fun getItemCount() = visitorList.size
}
