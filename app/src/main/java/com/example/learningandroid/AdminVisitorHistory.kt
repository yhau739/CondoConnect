package com.example.learningandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.VisitorModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminVisitorHistory : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var visitorAdapter: VisitorHistoryAdapter
    private lateinit var database: DatabaseReference
    private val visitorList = mutableListOf<VisitorModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_visitor_history)

        // Find the RecyclerView by ID
        recyclerView = findViewById(R.id.recyclerViewAdminVisitorHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter and set it to the RecyclerView
        visitorAdapter = VisitorHistoryAdapter(visitorList, null, true)
        recyclerView.adapter = visitorAdapter

        // Initialize the Firebase database reference
        database = MainActivity.getDatabase().getReference("Visitors")

        // Fetch the visitors
        fetchVisitors()

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }
    }

//    private fun fetchVisitors() {
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        val currentDate = Date()
//
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                visitorList.clear()
//                for (data in snapshot.children) {
//                    val visitor = data.getValue(VisitorModel::class.java)
//                    if (visitor != null && visitor.status == "Checked Out") {
////                        visitorList.add(visitor)
//                        try {
//                            val validTillDate = dateFormat.parse(visitor.validTill)
//                            if (validTillDate != null && validTillDate.before(currentDate)) {
//                                visitor.id = data.key ?: ""
//                                visitorList.add(visitor)
//                            }
//                        } catch (e: ParseException) {
//                            // Handle parse exception if the date format is incorrect
//                        }
//                    }
//                }
//                visitorAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle database error
//            }
//        })
//    }
    private fun fetchVisitors() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                visitorList.clear()
                for (data in snapshot.children) {
                    val visitor = data.getValue(VisitorModel::class.java)
                    if (visitor != null) {
                        val addVisitor = try {
                            val validTillDate = dateFormat.parse(visitor.validTill)
                            validTillDate != null && validTillDate.before(currentDate)
                        } catch (e: ParseException) {
                            false // If date parsing fails, do not add the visitor
                        }

                        if (visitor.status == "Checked Out" || addVisitor) {
                            visitor.id = data.key ?: ""
                            visitorList.add(visitor)
                        }
                    }
                }
                visitorAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

}
