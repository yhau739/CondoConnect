package com.example.learningandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.learningandroid.models.Bill
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class AdminGenerateBill : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_generate_bill)

        database = MainActivity.getDatabase().reference

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val yearInput = findViewById<EditText>(R.id.year_input)
        val amountInput = findViewById<EditText>(R.id.bill_amount_input)
        val generateButton = findViewById<Button>(R.id.generate_button)

        backArrow.setOnClickListener {
            finish()
        }


        generateButton.setOnClickListener {
            val inputYear = yearInput.text.toString()
            val inputAmount = amountInput.text.toString()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            if (inputYear.isNotEmpty()) {
                val year = inputYear.toInt()
                if (year > currentYear) {
                    if (inputAmount.isNotEmpty()) {
                        val amount = inputAmount.toIntOrNull()
                        if (amount != null && amount > 0) {
                            checkYearExists(year) { exists ->
                                if (exists) {
                                    Toast.makeText(
                                        this,
                                        "Year $year already exists in the database.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val bills = generateBills(year, amount)
                                    insertBillsIntoFirebase(bills, year)
                                    Toast.makeText(
                                        this,
                                        "Bills generated for year $year",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Please enter a valid positive integer for the bill amount.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, "Please enter the bill amount.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please enter a year after $currentYear",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Please enter a year", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkYearExists(year: Int, callback: (Boolean) -> Unit) {
        database.child("Units").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var yearExists = false
                for (unitSnapshot in snapshot.children) {
                    val billingSnapshot = unitSnapshot.child("billing").child(year.toString())
                    if (billingSnapshot.exists()) {
                        yearExists = true
                        break
                    }
                }
                callback(yearExists)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminGenerateBill, "Database error: ${error.message}",
                    Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }

    private fun generateBills(year: Int, amount: Int): List<Bill> {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val bills = mutableListOf<Bill>()
        for (month in months) {
            bills.add(Bill(month, year, amount.toDouble(), "Unpaid"))
        }
        return bills
    }

    private fun insertBillsIntoFirebase(bills: List<Bill>, year: Int) {
        val unitsRef = database.child("Units")
        unitsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (unitSnapshot in snapshot.children) {
                    val unitKey = unitSnapshot.key ?: continue
                    val billingRef = unitsRef.child(unitKey).child("billing").child(year.toString())
                    for (bill in bills) {
                        billingRef.child(bill.month).setValue(bill)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminGenerateBill, "Database error: ${error.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

}