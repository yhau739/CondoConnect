package com.example.learningandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.VisitorModel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var visitorAdapter: VisitorAdapter
    private lateinit var database: DatabaseReference
    private val visitorList = mutableListOf<VisitorModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewUpcoming)
        recyclerView.layoutManager = LinearLayoutManager(context)

        visitorAdapter = VisitorAdapter(visitorList, null, false)
        recyclerView.adapter = visitorAdapter

        database = MainActivity.getDatabase().getReference("Visitors")
        fetchVisitors()

        return view
    }

    private fun fetchVisitors() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()

        // Load unit number from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences(
            "my.PREFERENCE_FILE_KEY",
            AppCompatActivity.MODE_PRIVATE
        )
        val unitNo = sharedPreferences.getString("unitNumber", "") ?: ""

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                visitorList.clear()
                for (data in snapshot.children) {
                    val visitor = data.getValue(VisitorModel::class.java)
                    if (visitor != null && visitor.unitNo == unitNo && visitor.status != "Checked Out") {
                        try {
                            val validTillDate = dateFormat.parse(visitor.validTill)
                            if (validTillDate != null && validTillDate.after(currentDate)) {
                                visitor.id = data.key ?: ""
                                visitorList.add(visitor)
                            }
                        } catch (e: Exception) {
                            // Handle parse exception or any other exception
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
//        // Load unit number from SharedPreferences
//        val sharedPreferences = requireContext().getSharedPreferences("my.PREFERENCE_FILE_KEY",
//            AppCompatActivity.MODE_PRIVATE
//        )
//        val unitNo = sharedPreferences.getString("unitNumber", "") ?: ""
//
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                visitorList.clear()
//                for (data in snapshot.children) {
//                    val visitor = data.getValue(VisitorModel::class.java)
//                    if (visitor != null && visitor.unitNo == unitNo && visitor.status != "Checked Out") {
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

    private fun isUpcoming(validTill: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val date = dateFormat.parse(validTill)
            date != null && date.after(Date())
        } catch (e: Exception) {
            false
        }
    }
}
