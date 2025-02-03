package com.example.learningandroid

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.DocumentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class UserDocumentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var documentAdapter: UserDocumentAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_documents)

        // Initialize the back arrow
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewDocuments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = MainActivity.getDatabase().reference.child("Documents")

        fetchDocuments()
    }

    private fun fetchDocuments() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val documents = mutableListOf<DocumentModel>()
                for (documentSnapshot in dataSnapshot.children) {
                    val document = documentSnapshot.getValue(DocumentModel::class.java)
                    if (document != null) {
                        documents.add(document)
                    }
                }
                documentAdapter = UserDocumentAdapter(documents)
                recyclerView.adapter = documentAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}
