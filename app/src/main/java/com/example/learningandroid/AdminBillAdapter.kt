package com.example.learningandroid

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FetchBill
import com.google.firebase.database.DatabaseReference

class AdminBillAdapter(
    private val bills: List<FetchBill>,
    private val database: DatabaseReference
) : RecyclerView.Adapter<AdminBillAdapter.AdminBillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item_bill, parent, false)
        return AdminBillViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminBillViewHolder, position: Int) {
        val bill = bills[position]
        holder.bind(bill)
    }

    override fun getItemCount(): Int = bills.size

    inner class AdminBillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBillUnit: TextView = itemView.findViewById(R.id.tvBillUnit)
        private val tvBillMonth: TextView = itemView.findViewById(R.id.tvBillMonth)
        private val tvBillYear: TextView = itemView.findViewById(R.id.tvBillYear)
        private val tvBillAmount: TextView = itemView.findViewById(R.id.tvBillAmount)
        private val tvBillStatus: TextView = itemView.findViewById(R.id.tvBillStatus)
        private val btnApproveAction: Button = itemView.findViewById(R.id.btnApproveAction)
        private val btnDisapproveAction: Button = itemView.findViewById(R.id.btnDisapproveAction)

        fun bind(bill: FetchBill) {
            tvBillUnit.text = bill.unitId
            tvBillMonth.text = bill.month
            tvBillYear.text = bill.year.toString()
            tvBillAmount.text = "Amount: RM${bill.amount}"
            tvBillStatus.text = "Status: ${bill.status}"

            btnApproveAction.setOnClickListener {
                showConfirmationDialog(bill, true)
            }

            btnDisapproveAction.setOnClickListener {
                showConfirmationDialog(bill, false)
            }
        }

        private fun showConfirmationDialog(bill: FetchBill, isApprove: Boolean) {
            val message = if (isApprove) {
                "Are you sure you want to approve this bill? \n  \nYYK SDN BHD\n" +
                        "Bank: CIMB\n" +
                        "Acc No: 28372671\n" +
                        "Amount: RM 120\n" +
                        "Reference: ${bill.unitId} ${bill.year} ${bill.month}"
            } else {
                "Are you sure you want to disapprove this bill? \n  \nYYK SDN BHD\n"  +
                        "Bank: CIMB\n" +
                        "Acc No: 28372671\n" +
                        "Amount: RM 120\n" +
                        "Reference: ${bill.unitId} ${bill.year} ${bill.month}"
            }
            AlertDialog.Builder(itemView.context)
                .setTitle(if (isApprove) "Approve Bill" else "Disapprove Bill")
                .setMessage(message)
                .setPositiveButton("Yes") { dialog, _ ->
                    updateBillStatus(bill, isApprove)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        private fun updateBillStatus(bill: FetchBill, isApprove: Boolean) {
            val newStatus = if (isApprove) "Paid" else "Unpaid"
            database.child("Units").child(bill.unitId).child("billing").child(bill.year.toString()).child(bill.month).child("status").setValue(newStatus)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(itemView.context, "Bill status updated to $newStatus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(itemView.context, "Failed to update bill status", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
