package com.example.learningandroid

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learningandroid.models.NoticeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class AddNoticeActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etSubtitle: EditText
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var etContent: EditText
    private lateinit var ivNoticeImage: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnAddNotice: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notice)

        etTitle = findViewById(R.id.etTitle)
        etSubtitle = findViewById(R.id.etSubtitle)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        etContent = findViewById(R.id.etContent)
        ivNoticeImage = findViewById(R.id.ivNoticeImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnAddNotice = findViewById(R.id.btnAddNotice)

        auth = FirebaseAuth.getInstance()
        database = MainActivity.getDatabase().getReference("Notices")
        storageReference = MainActivity.getStorage().reference.child("notice_images")

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading notice...")
        progressDialog.setCancelable(false)

        // back arrow
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }

        btnSelectImage.setOnClickListener {
            openFileChooser()
        }

        btnAddNotice.setOnClickListener {
            // Validate that the required fields are not empty
            val title = etTitle.text.toString().trim()
            val subtitle = etSubtitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty() || subtitle.isEmpty() || description.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Don't leave anything blank", Toast.LENGTH_SHORT).show()
            } else {
                uploadNotice()
            }
        }

        setCurrentDateTime()
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            ivNoticeImage.setImageURI(imageUri)
        }
    }

    private fun uploadNotice() {
        if (imageUri != null) {
            progressDialog.show() // Show the loader
            val fileReference = storageReference.child(System.currentTimeMillis().toString() + ".jpg")
            fileReference.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val noticeId = database.push().key
                        val notice = noticeId?.let {
                            auth.currentUser?.let { it1 ->
                                NoticeModel(
                                    it1.uid,
                                    etTitle.text.toString(),
                                    etSubtitle.text.toString(),
                                    etDate.text.toString(),
                                    etDescription.text.toString(),
                                    etContent.text.toString(),
                                    uri.toString()
                                )
                            }
                        }
                        noticeId?.let { database.child(it).setValue(notice) }
                        progressDialog.dismiss() // Dismiss the loader
                        Toast.makeText(this, "Notice added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss() // Dismiss the loader
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCurrentDateTime() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a", Locale.getDefault())
        val currentDateTime = dateFormat.format(calendar.time)
        etDate.setText(currentDateTime)
    }

}
