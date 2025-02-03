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
import com.bumptech.glide.Glide
import com.example.learningandroid.models.ClubModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddClubActivity : AppCompatActivity() {
    private lateinit var clubTitleEditText: EditText
    private lateinit var clubDescriptionEditText: EditText
    private lateinit var clubImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var addClubButton: Button

    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_club)

        clubTitleEditText = findViewById(R.id.etClubTitle)
        clubDescriptionEditText = findViewById(R.id.etClubDescription)
        clubImageView = findViewById(R.id.ivClubImage)
        selectImageButton = findViewById(R.id.btnSelectImage)
        addClubButton = findViewById(R.id.btnAddClub)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Club...")
        progressDialog.setCancelable(false)

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        addClubButton.setOnClickListener {
            val title = clubTitleEditText.text.toString()
            val description = clubDescriptionEditText.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (selectedImageUri != null) {
                    uploadImageToFirebase(title, description)
                } else {
                    addClubToDatabase(title, description, "")
                }
            }
        }

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }
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
            selectedImageUri = data.data
            Glide.with(this).load(selectedImageUri).into(clubImageView)
        }
    }

    private fun uploadImageToFirebase(title: String, description: String) {
        progressDialog.show() // Show the loader
        val storageReference = MainActivity.getStorage().getReference("club_images/${UUID.randomUUID()}")
        selectedImageUri?.let {
            storageReference.putFile(it).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    addClubToDatabase(title, description, uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addClubToDatabase(title: String, description: String, imageUrl: String) {
        val database = MainActivity.getDatabase().reference
        val clubId = database.push().key ?: return

        val newClub = ClubModel(id = clubId, title = title, description = description, imageUrl = imageUrl)
        database.child("Clubs").child(clubId).setValue(newClub).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Club added successfully", Toast.LENGTH_SHORT).show()
                // Redirect to Facility page
                val intent = Intent(this, AdminManageClub::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                progressDialog.dismiss() // Dismiss the loader
            } else {
                Toast.makeText(this, "Failed to add club", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss() // Dismiss the loader
            }
        }
    }
}
