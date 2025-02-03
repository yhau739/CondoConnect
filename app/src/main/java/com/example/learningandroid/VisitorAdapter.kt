package com.example.learningandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.VisitorModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VisitorAdapter(
    private val visitorList: List<VisitorModel>,
    private val listener: OnVisitorClickListener?,
    private val isAdmin: Boolean ) :
    RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder>() {

    interface OnVisitorClickListener {
        fun onVisitorClick(visitor: VisitorModel)
    }

    class VisitorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val visitorName: TextView = itemView.findViewById(R.id.tvVisitorName)
        val visitorCode: TextView = itemView.findViewById(R.id.tvVisitorCode)
        val visitorStatus: Button = itemView.findViewById(R.id.tvVisitorStatus)
        val vehicleNo: TextView = itemView.findViewById(R.id.tvVehicleNo)
        val visitorButtonType: Button = itemView.findViewById(R.id.btnVisitorType)
        val visitorValidTill: TextView = itemView.findViewById(R.id.tvVisitorValidTill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visitor, parent, false)
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
        } else {
            // Reset to default colors if needed
            holder.visitorStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.visitorStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.darkergreen))
        }

        holder.vehicleNo.text = visitor.vehicleNo
        holder.visitorValidTill.text = "Valid till: ${visitor.validTill}"

        // Set button text based on isAdmin flag
        if (isAdmin) {
            holder.visitorButtonType.text = "Check In/Out"
        } else {
            holder.visitorButtonType.text = "Regular Visitor"
        }

        holder.visitorButtonType.setOnClickListener {
            listener?.onVisitorClick(visitor)
        }
    }

    override fun getItemCount() = visitorList.size
}
