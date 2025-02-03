package com.example.learningandroid

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learningandroid.models.VisitorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddVisitorActivity : AppCompatActivity() {

    private lateinit var etVisitorName: EditText
    private lateinit var etUnitNo: EditText
    private lateinit var etRegisteredBy: EditText
    private lateinit var etContactNo: EditText
    private lateinit var etVehicleNo: EditText
    private lateinit var etValidTill: EditText
    private lateinit var btnAddVisitor: Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var registeredByUsername: String
    private lateinit var unitNo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visitor)

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance()
        database = MainActivity.getDatabase().reference

        etVisitorName = findViewById(R.id.etVisitorName)
        etUnitNo = findViewById(R.id.etUnitNo)
        etRegisteredBy = findViewById(R.id.etRegisteredBy)
        etContactNo = findViewById(R.id.etContactNo)
        etVehicleNo = findViewById(R.id.etVehicleNo)
        etValidTill = findViewById(R.id.etValidTill)
        btnAddVisitor = findViewById(R.id.btnAddVisitor)

        // Load unit number from SharedPreferences
        val sharedPreferences = getSharedPreferences("my.PREFERENCE_FILE_KEY", MODE_PRIVATE)
        unitNo = sharedPreferences.getString("unitNumber", "") ?: ""
        etUnitNo.setText(unitNo)

        // Load registered by username from Firebase Database
        val userId = auth.currentUser?.uid ?: ""
        database.child("Users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            registeredByUsername = dataSnapshot.child("username").value.toString()
            etRegisteredBy.setText(registeredByUsername)
        }

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        etValidTill.setOnClickListener {
            showDatePickerDialog()
        }

        btnAddVisitor.setOnClickListener {
            addVisitor()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                etValidTill.setText(sdf.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun addVisitor() {
        val name = etVisitorName.text.toString().trim()
        val contactNo = etContactNo.text.toString().trim()
        val vehicleNo = etVehicleNo.text.toString().trim()
        val validTill = etValidTill.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            etVisitorName.error = "Please enter visitor name"
            return
        }

        if (TextUtils.isEmpty(contactNo)) {
            etContactNo.error = "Please enter contact number"
            return
        }

        if (TextUtils.isEmpty(vehicleNo)) {
            etVehicleNo.error = "Please enter vehicle number"
            return
        }

        if (TextUtils.isEmpty(validTill)) {
            etValidTill.error = "Please select a valid till date"
            return
        }

        // Generate unique visitor code
        val visitorCode = generateVisitorCode()

        // Create a new reference for the visitor and get the generated ID
        val newVisitorRef = database.child("Visitors").push()
        val visitorId = newVisitorRef.key ?: ""

        // Create a new VisitorModel object with the generated ID
        val visitor = VisitorModel(
            id = visitorId,
            name = name,
            unitNo = unitNo,
            registeredBy = registeredByUsername,
            contactNo = contactNo,
            vehicleNo = vehicleNo,
            status = "Active",
            validTill = validTill,
            visitorCode = visitorCode,
            checkInTime = "",
            checkOutTime = ""
        )

        // Set the value for the new visitor in the database
        newVisitorRef.setValue(visitor).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Visitor added successfully, " +
                        "unique code is: ${visitor.visitorCode}", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add visitor: ${task.exception?.message}"
                    , Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun generateVisitorCode(): String {
        val letters = ('A'..'Z') + ('a'..'z')
        val digits = ('0'..'9')
        return (1..4).map { letters.random() }.joinToString("") +
                (1..4).map { digits.random() }.joinToString("")
    }
}
