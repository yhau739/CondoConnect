package com.example.learningandroid

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.Bill
import com.example.learningandroid.models.FetchBill
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class OutstandingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var billAdapter: BillAdapter
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outstanding, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewOutstanding)
        recyclerView.layoutManager = LinearLayoutManager(context)

        database = MainActivity.getDatabase().getReference()

        fetchBills()

        return view
    }

    private fun fetchBills() {
        val sharedPref = requireContext().getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val unitNumber = sharedPref.getString("unitNumber", "Unknown")

        if (unitNumber == "Unknown") {
            // Handle case where unit number is not found in shared preferences
            return
        }

        database.child("Units").child(unitNumber!!).child("billing").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bills = mutableListOf<FetchBill>()
                for (yearSnapshot in dataSnapshot.children) {
                    for (monthSnapshot in yearSnapshot.children) {
                        val bill = monthSnapshot.getValue(FetchBill::class.java)
                        if (bill != null && bill.status == "Unpaid") {
                            bills.add(
                                FetchBill(
                                    month = bill.month,
                                    year = bill.year,
                                    amount = bill.amount,
                                    status = bill.status,
                                    unitId = unitNumber
                                )
                            )
                        }
                    }
                }
                billAdapter = BillAdapter(bills, { bill ->
                    // Handle the button click for outstanding bills
//                    Toast.makeText(context, "I have paid ${bill.month}", Toast.LENGTH_SHORT).show()
                    context?.let { showConfirmationDialog(it, bill) }
                }, "Pay")
                recyclerView.adapter = billAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun showConfirmationDialog(context: Context, bill: FetchBill) {
        val sharedPref = requireContext().getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val unitNumber = sharedPref.getString("unitNumber", "Unknown")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Payment Details")
        builder.setMessage("YYK SDN BHD\nBank: CIMB\nAcc No: 28372671\nAmount: RM 120\nReference: ${unitNumber} ${bill.year} ${bill.month}")

        builder.setPositiveButton("I have paid") { dialog, which ->
            // modify db bill status
            onPayClick(bill)
            Toast.makeText(context, "Request Submitted, awaiting approval ...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Back") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun onPayClick(bill: FetchBill) {
        val sharedPref = requireContext().getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val unitNumber = sharedPref.getString("unitNumber", "Unknown")

        if (unitNumber == "Unknown") {
            // Handle case where unit number is not found in shared preferences
            return
        }

        // Update the status to "Waiting Approval"
        database.child("Units")
            .child(unitNumber!!)
            .child("billing")
            .child(bill.year.toString())
            .child(bill.month)
            .child("status")
            .setValue("Waiting Approval")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Status updated to Waiting Approval", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

