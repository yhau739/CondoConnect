package com.example.learningandroid

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
import com.google.firebase.database.ValueEventListener


class TransactionHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var billAdapter: BillAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewTransactionHistory)
        recyclerView.layoutManager = LinearLayoutManager(context)

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

        val database = MainActivity.getDatabase().getReference()

        database.child("Units").child(unitNumber!!).child("billing").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bills = mutableListOf<FetchBill>()
                for (yearSnapshot in dataSnapshot.children) {
                    for (monthSnapshot in yearSnapshot.children) {
                        val bill = monthSnapshot.getValue(FetchBill::class.java)
                        if (bill != null && bill.status == "Paid") {
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
                    // Handle the button click for transaction history bills
                })
                recyclerView.adapter = billAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

}

