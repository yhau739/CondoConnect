package com.example.learningandroid

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FeedbackModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SubmitFeedback : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private lateinit var spinnerFeedbackType: Spinner
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var ivFeedbackImage: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSubmitFeedback: Button
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_feedback)

        // Initialize Firebase components
        database = MainActivity.getDatabase().reference.child("Feedback")
        auth = FirebaseAuth.getInstance()
        storage = MainActivity.getStorage()

        // Initialize views
        spinnerFeedbackType = findViewById(R.id.spinnerFeedbackType)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        ivFeedbackImage = findViewById(R.id.ivFeedbackImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading feedback...")
        progressDialog.setCancelable(false)

        // Set up spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.feedback_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFeedbackType.adapter = adapter
        }

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        btnSelectImage.setOnClickListener {
            selectImage()
        }

        btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                Glide.with(this).load(it).into(ivFeedbackImage)
            }
        }
    }

    private fun submitFeedback() {
        progressDialog.show()
        val feedbackType = spinnerFeedbackType.selectedItem.toString()
        val title = etTitle.text.toString()
        val content = etContent.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss() // Dismiss the loader
            return
        }

        val feedbackId = database.push().key ?: return
        val feedback = auth.currentUser?.uid?.let {
            FeedbackModel(
                feedbackId,
                feedbackType,
                title,
                content,
                "",
                it,
                "unresolved"
            )
        }

        if (selectedImageUri != null) {
            val imageRef = storage.reference.child("feedback_images/$feedbackId.jpg")
            imageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    if (feedback != null) {
                        feedback.imageUrl = uri.toString()
                    }
                    if (feedback != null) {
                        saveFeedback(feedback)
                    }
                }
            }.addOnFailureListener {
                progressDialog.dismiss() // Dismiss the loader
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (feedback != null) {
                saveFeedback(feedback)
            }
        }
    }

    private fun saveFeedback(feedback: FeedbackModel) {
        database.child(feedback.id).setValue(feedback).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss() // Dismiss the loader
                Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                progressDialog.dismiss() // Dismiss the loader
                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
