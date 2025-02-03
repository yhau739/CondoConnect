package com.example.learningandroid

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.DocumentModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class admin_documents : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var documentAdapter: AdminDocumentAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private val PICK_PDF_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_documents)

        // Initialize the back arrow
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewDocuments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = MainActivity.getDatabase().reference.child("Documents")

        fetchDocuments()

        // Initialize the ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading PDF...")
        progressDialog.setCancelable(false)

        // Set up the FAB to add a new document
        findViewById<FloatingActionButton>(R.id.fabAddDocument).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PICK_PDF_CODE)
        }
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
                documentAdapter = AdminDocumentAdapter(documents, database)
                recyclerView.adapter = documentAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_CODE && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                val fileName = getFileName(uri)
                uploadPDF(uri, fileName)
            }
        }
    }

    private fun uploadPDF(uri: Uri, fileName: String) {
        // Show progress dialog
        progressDialog.show()
        val storageReference = MainActivity.getStorage().reference.child("documents/${UUID.randomUUID()}.pdf")
        val uploadTask = storageReference.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                val documentUrl = uri.toString()
                saveDocumentInfoToDatabase(documentUrl, fileName)
                progressDialog.dismiss()  // Dismiss progress dialog
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()  // Dismiss progress dialog
        }
    }

    private fun saveDocumentInfoToDatabase(documentUrl: String, fileName: String) {
        val documentId = database.push().key
        val document = DocumentModel(documentId,
            auth.currentUser?.uid, fileName, "PDF Document", documentUrl)

        if (documentId != null) {
            database.child(documentId).setValue(document).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Document saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save document", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unknown"
    }
}