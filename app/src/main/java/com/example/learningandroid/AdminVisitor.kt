package com.example.learningandroid

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.VisitorModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminVisitor : AppCompatActivity(), VisitorAdapter.OnVisitorClickListener {
    private lateinit var visitorAdapter: VisitorAdapter
    private lateinit var database: DatabaseReference
    private val visitorList = mutableListOf<VisitorModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_visitor)

        // Find RecyclerView by ID
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAdminVisitor)

        // Set up LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = MainActivity.getDatabase().getReference("Visitors")

        // Set up Adapter with the listener
        visitorAdapter = VisitorAdapter(visitorList, this, true)
        recyclerView.adapter = visitorAdapter

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        // Fetch visitors from the database
        fetchVisitors()
    }

    private fun fetchVisitors() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                visitorList.clear()
                for (data in snapshot.children) {
                    val visitor = data.getValue(VisitorModel::class.java)
                    if (visitor != null && visitor.status != "Checked Out") {
                        try {
                            val validTillDate = dateFormat.parse(visitor.validTill)
                            if (validTillDate != null && validTillDate.after(currentDate)) {
                                visitor.id = data.key ?: ""
                                visitorList.add(visitor)
                            }
                        } catch (e: ParseException) {
                            // Handle parse exception if the date format is incorrect
                        }
                    }
                }
                visitorAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


//    private fun fetchVisitors() {
//
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                visitorList.clear()
//                for (data in snapshot.children) {
//                    val visitor = data.getValue(VisitorModel::class.java)
//                    if (visitor != null && visitor.status != "Checked Out") {
//                        visitor.id = data.key ?: ""
//                        visitorList.add(visitor)
//                    }
//                }
//                visitorAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
//    }

    override fun onVisitorClick(visitor: VisitorModel) {
        // Handle the click event for admin activity
        // Show the dialog with Check In and Check Out options
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_check_in_out)

        val btnCheckIn: Button = dialog.findViewById(R.id.btnCheckIn)
        val btnCheckOut: Button = dialog.findViewById(R.id.btnCheckOut)

        btnCheckIn.setOnClickListener {
            // Validate before checking in
            database.child(visitor.id).child("status").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentStatus = dataSnapshot.getValue(String::class.java)
                    if (currentStatus == "Checked In") {
                        Toast.makeText(this@AdminVisitor, "${visitor.name} is already Checked In.", Toast.LENGTH_SHORT).show()
                    } else if (currentStatus == "Checked Out") {
                        Toast.makeText(this@AdminVisitor, "${visitor.name} is already Checked Out.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle Check In click
                        val checkInTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                        database.child(visitor.id).child("status").setValue("Checked In")
                        database.child(visitor.id).child("checkInTime").setValue(checkInTime)
                        database.child(visitor.id).child("checkOutTime").setValue("")

                        Toast.makeText(this@AdminVisitor, "Checked In: ${visitor.name}", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }

        btnCheckOut.setOnClickListener {
            // Validate before checking out
            database.child(visitor.id).child("status").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentStatus = dataSnapshot.getValue(String::class.java)
                    if (currentStatus != "Checked In") {
                        Toast.makeText(this@AdminVisitor, "${visitor.name} is not currently Checked In.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle Check Out click
                        val checkOutTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                        database.child(visitor.id).child("status").setValue("Checked Out")
                        database.child(visitor.id).child("checkOutTime").setValue(checkOutTime)

                        Toast.makeText(this@AdminVisitor, "Checked Out: ${visitor.name}", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }

        dialog.show()
    }
}
