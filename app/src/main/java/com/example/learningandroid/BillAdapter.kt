package com.example.learningandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.Bill
import com.example.learningandroid.models.FetchBill

class BillAdapter(
    private val bills: List<FetchBill>,
    private val onButtonClick: (FetchBill) -> Unit, // Adding a click listener
    private val buttonText: String? = null // Optional button text
) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.bind(bill, onButtonClick, buttonText)
    }

    override fun getItemCount(): Int {
        return bills.size
    }

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBillMonth: TextView = itemView.findViewById(R.id.tvBillMonth)
        private val tvBillYear: TextView = itemView.findViewById(R.id.tvBillYear)
        private val tvBillAmount: TextView = itemView.findViewById(R.id.tvBillAmount)
        private val tvBillStatus: TextView = itemView.findViewById(R.id.tvBillStatus)
        private val btnBillAction: Button = itemView.findViewById(R.id.btnBillAction)

        fun bind(bill: FetchBill, onButtonClick: (FetchBill) -> Unit, buttonText: String?) {
            tvBillMonth.text = bill.month
            tvBillYear.text = bill.year.toString()
            tvBillAmount.text = "Amount: RM${bill.amount}"
            tvBillStatus.text = "Status: ${bill.status}"

            if (buttonText != null) {
                btnBillAction.text = buttonText
                btnBillAction.visibility = View.VISIBLE
                btnBillAction.setOnClickListener {
                    onButtonClick(bill)
                }
            } else {
                btnBillAction.visibility = View.GONE
            }
        }
    }
}
