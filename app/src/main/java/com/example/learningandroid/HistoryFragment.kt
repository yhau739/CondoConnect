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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var visitorAdapter: VisitorHistoryAdapter
    private lateinit var database: DatabaseReference
    private val visitorList = mutableListOf<VisitorModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(context)

        visitorAdapter = VisitorHistoryAdapter(visitorList, null, false)
        recyclerView.adapter = visitorAdapter

        database = MainActivity.getDatabase().getReference("Visitors")

        fetchVisitors()

        return view
    }

    private fun fetchVisitors() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()

        // Load unit number from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("my.PREFERENCE_FILE_KEY",
            AppCompatActivity.MODE_PRIVATE
        )
        val unitNo = sharedPreferences.getString("unitNumber", "") ?: ""

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

                        if (visitor.unitNo == unitNo && (visitor.status == "Checked Out" || addVisitor)) {
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
//                    if (visitor != null && visitor.unitNo == unitNo && visitor.status == "Checked Out") {
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

    private fun isHistory(validTill: String): Boolean {
        // Implement your logic to determine if the visitor is history
        return true
    }
}
