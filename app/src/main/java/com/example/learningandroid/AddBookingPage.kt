package com.example.learningandroid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FacilityBookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddBookingPage : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etBookingTitle: EditText
    private lateinit var etBookingDate: EditText
    private lateinit var etBookingStartTime: EditText
    private lateinit var rgBookingDuration: RadioGroup
    private lateinit var rbOneHour: RadioButton
    private lateinit var rbTwoHours: RadioButton
    private lateinit var btnAddBooking: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_booking_page)

        // Initialize database reference
        database = MainActivity.getDatabase().getReference("Facilities")
        auth = FirebaseAuth.getInstance()

        // Initialize views
        etBookingTitle = findViewById(R.id.etBookingTitle)
        etBookingDate = findViewById(R.id.etBookingDate)
        etBookingStartTime = findViewById(R.id.etBookingStartTime)
        rgBookingDuration = findViewById(R.id.rgBookingDuration)
        rbOneHour = findViewById(R.id.rbOneHour)
        rbTwoHours = findViewById(R.id.rbTwoHours)
        btnAddBooking = findViewById(R.id.btnAddBooking)

        // Handle back button click
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

        // Get data from intent and populate fields
        val facilityId = intent.getStringExtra("facilityId") ?: ""
        val facilityTitle = intent.getStringExtra("facilityTitle") ?: ""
        val facilityImageUrl = intent.getStringExtra("facilityImageUrl") ?: ""

        // preset the values
        etBookingTitle.setText(facilityTitle)
        val imageView: ImageView = findViewById(R.id.courtImage)
        Glide.with(this).load(facilityImageUrl).into(imageView)

        // Date and Time Pickers
        etBookingDate.setOnClickListener {
            showDatePickerDialog { date ->
                etBookingDate.setText(date)
            }
        }

        etBookingStartTime.setOnClickListener {
            showTimePickerDialog { time ->
                etBookingStartTime.setText(time)
            }
        }

        // Handle add booking button click
        btnAddBooking.setOnClickListener {
            if (validateFields()) {
                checkBookingClash(facilityId, facilityTitle, facilityImageUrl)
            }
        }
    }

    private fun validateFields(): Boolean {
        if (etBookingTitle.text.isNullOrEmpty()) {
            etBookingTitle.error = "Booking title is required"
            return false
        }
        if (etBookingDate.text.isNullOrEmpty()) {
            etBookingDate.error = "Booking date is required"
            return false
        }
        if (etBookingStartTime.text.isNullOrEmpty()) {
            etBookingStartTime.error = "Booking start time is required"
            return false
        }
        if (rgBookingDuration.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select a booking duration", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkBookingClash(courtId: String, title: String, imageUrl: String) {
        val bookingDate = etBookingDate.text.toString()
        val bookingStartTime = etBookingStartTime.text.toString()
        val bookingEndTime = calculateEndTime(bookingStartTime)

        database.child(courtId).child("bookings").orderByChild("bookingDate").equalTo(bookingDate)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isClash = false
                    for (dataSnapshot in snapshot.children) {
                        val existingBooking = dataSnapshot.getValue(FacilityBookingModel::class.java)
                        if (existingBooking != null) {
                            val existingStart = existingBooking.bookingStartTime
                            val existingEnd = existingBooking.bookingEndTime
                            if (isTimeOverlap(bookingStartTime, bookingEndTime, existingStart, existingEnd)) {
                                isClash = true
                                break
                            }
                        }
                    }

                    if (isClash) {
                        Toast.makeText(this@AddBookingPage,
                            "Booking time clashes with an existing booking", Toast.LENGTH_SHORT).show()
                    } else {
                        addBooking(courtId, title, imageUrl)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddBookingPage,
                        "Failed to check booking clash", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun isTimeOverlap(start1: String, end1: String, start2: String, end2: String): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime1 = sdf.parse(start1) ?: return false
        val endTime1 = sdf.parse(end1) ?: return false
        val startTime2 = sdf.parse(start2) ?: return false
        val endTime2 = sdf.parse(end2) ?: return false

        return startTime1.before(endTime2) && startTime2.before(endTime1)
    }

    private fun addBooking(courtId: String, title: String, imageUrl: String) {
        val bookingTitle = etBookingTitle.text.toString()
        val bookingDate = etBookingDate.text.toString()
        val bookingStartTime = etBookingStartTime.text.toString()
        val bookingEndTime = calculateEndTime(bookingStartTime)
        val bookingStatus = "Waiting Approval"
        val uid = auth.currentUser?.uid ?: ""

        val booking = FacilityBookingModel(
            uid = uid,
            bookingDate = bookingDate,
            bookingStartTime = bookingStartTime,
            bookingEndTime = bookingEndTime,
            bookingStatus = bookingStatus,
            imageUrl = imageUrl,
            title = title
        )


        database.child(courtId).child("bookings").push().setValue(booking)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Booked successfully", Toast.LENGTH_SHORT).show()
                    // Redirect to Facility page
                    val intent = Intent(this, Facility::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add booking", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun calculateEndTime(startTime: String): String {
        val duration = when (rgBookingDuration.checkedRadioButtonId) {
            R.id.rbOneHour -> 1
            R.id.rbTwoHours -> 2
            else -> 0
        }
        val parts = startTime.split(":").map { it.toInt() }
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, parts[0])
        calendar.set(Calendar.MINUTE, parts[1])
        calendar.add(Calendar.HOUR_OF_DAY, duration)
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSet(date)
        }, year, month, day)

        // Set the minimum date to one day after the current date
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = 0

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:00", selectedHour, selectedMinute)
            onTimeSet(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
