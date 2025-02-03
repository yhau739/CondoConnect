package com.example.learningandroid

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.FetchBill
import com.google.firebase.database.*

class AdminApproveBill : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var billAdapter: AdminBillAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_approve_bill)

        recyclerView = findViewById(R.id.recyclerViewBills)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = MainActivity.getDatabase().getReference()

        // back arrow
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }

        fetchBills()
    }

    private fun fetchBills() {
        database.child("Units").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bills = mutableListOf<FetchBill>()
                for (unitSnapshot in dataSnapshot.children) {
                    val unitId = unitSnapshot.key.toString()  // Get the unit ID
                    for (yearSnapshot in unitSnapshot.child("billing").children) {
                        for (monthSnapshot in yearSnapshot.children) {
                            val bill = monthSnapshot.getValue(FetchBill::class.java)
                            if (bill != null && bill.status == "Waiting Approval") {
                                bills.add(
                                    FetchBill(
                                        month = bill.month,
                                        year = bill.year,
                                        amount = bill.amount,
                                        status = bill.status,
                                        unitId = unitId
                                    )
                                )
                            }
                        }
                    }
                }
                billAdapter = AdminBillAdapter(bills, database)
                recyclerView.adapter = billAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}
