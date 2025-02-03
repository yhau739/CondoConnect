package com.example.learningandroid

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_CALL_PHONE = 1 // This is your unique request code
        private const val REQUEST_SMS_LOCATION_PERMISSIONS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPref = getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()


        // either use firebase auth get uid or use shared preference
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
//        if (currentUserId != null) {
//            displayUnitDetails(currentUserId, isOwnerLoggedIn)
//        }
        if (currentUserId != null) {
            checkIfOwner(currentUserId) { isOwner ->
                editor.putBoolean("isOwnerLoggedIn", isOwner)
                editor.apply()
                displayUnitDetails(currentUserId, isOwner)
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle home navigation
                    true
                }
                R.id.navigation_feedback -> {
                    // Handle club navigation
                    val intent = Intent(this, MyFeedback::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    // Handle profile navigation
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun checkIfOwner(userId: String, callback: (Boolean) -> Unit) {
        val database = MainActivity.getDatabase().reference
        database.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isOwner = dataSnapshot.child("owner").getValue(Boolean::class.java) ?: false
                callback(isOwner)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Home, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }

    fun onLogoutClick(view: View) {
        // Handle the click event
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show()

        // Clear shared preferences
        val sharedPreferences = view.context.getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Sign out from Firebase authentication
        FirebaseAuth.getInstance().signOut()

        // Redirect to login activity or any other desired activity
        val intent = Intent(view.context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        view.context.startActivity(intent)
    }


    fun onTalkToGuardClick(view: View) {
        // Handle the click event
        val phoneNumber = "tel:+601156273493"
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse(phoneNumber)
        }
        startActivity(dialIntent)
    }


    fun onTalkToManagementClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, SubmitFeedback::class.java))
        // Add your desired functionality here
    }

    fun onClubClick(view: View) {
        // Handle the click event
        showClubBottomSheetDialog()
    }


    fun onTalkToNeighbourClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, MyClubsActivity::class.java))
    }

    fun onEmergencyClick(view: View) {
        // Handle the click event
        showBottomSheetDialog()
    }

    fun onFacilityClick(view: View) {
        // Start the Facility activity
        val intent = Intent(this, Facility::class.java)
        startActivity(intent)
    }

    fun onVisitorClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, Visitor::class.java))
    }

    fun onNoticesClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, Notice::class.java))
    }

    fun onBillingClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, billing::class.java))
    }

    fun OnDocumentsClicked(view: View) {
        // Handle the click event
        // Navigate to Home
        startActivity(Intent(this, UserDocumentsActivity::class.java))
    }

    fun onFeedbackClick(view: View) {
        // Handle the click event
        // Navigate to Home
        startActivity(Intent(this, SubmitFeedback::class.java))
    }

    private fun showClubBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.club_bottom_sheet_layout,
            findViewById<LinearLayout>(R.id.club_bottom_sheet_container)
        )

        // Click listener for back arrow
        bottomSheetView.findViewById<ImageView>(R.id.backArrowSheet).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Click listener for Join New Clubs
        bottomSheetView.findViewById<LinearLayout>(R.id.join_club_container).setOnClickListener {
            startActivity(Intent(this, JoinClubsActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        // Click listener for My Clubs
        bottomSheetView.findViewById<LinearLayout>(R.id.my_clubs_container).setOnClickListener {
            startActivity(Intent(this, MyClubsActivity::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }


    // emergency function
    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.emergency_bottom_sheet_layout,
            findViewById<LinearLayout>(R.id.bottom_sheet_container)
        )

        // Click listener for back arrow
        bottomSheetView.findViewById<ImageView>(R.id.backArrowSheet).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.alert_guard_container).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
            }else{
                // Permission already granted, make the call
                makePhoneCall()
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.send_location_container).setOnClickListener {
            // send current location
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions()
            } else {
                sendEmergencySMS()
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    // phone call
    private fun makePhoneCall() {
        val phoneNumber = "tel:+601156273493"
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse(phoneNumber)
        }
        try {
            startActivity(callIntent)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    // send current location to guard
    private fun sendEmergencySMS() {
        if (isLocationEnabled()) {
            getCurrentLocation { locationMessage ->
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:+601156273493")
                    putExtra("sms_body", locationMessage)
                }
                startActivity(smsIntent)
            }
        } else {
            Toast.makeText(this, "Please enable location services to send your current location.", Toast.LENGTH_SHORT).show()
            promptEnableLocation()
        }
    }

    private fun displayUnitDetails(userId: String, isOwnerLoggedIn: Boolean) {
        val database = MainActivity.getDatabase().reference
        val sharedPref = getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // from shared preferences
        if (isOwnerLoggedIn) {
            // User is an owner, query the Units node to find their unit
            database.child("Units").orderByChild("ownerID").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Display the unit details
                        for (unitSnapshot in dataSnapshot.children) {
                            val unit = unitSnapshot.key
                            val ownerID = unitSnapshot.child("ownerID").value.toString()
                            // Display the unit details
                            findViewById<TextView>(R.id.unitNumberTextView).text = "$unit"
                            editor.putString("unitNumber", unit)
                            editor.apply()
//                            Toast.makeText(this@Home, "Unit: $unit, Owner: $ownerID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle case where no unit is found for the owner
                        Toast.makeText(this@Home, "No unit found for this owner.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Home, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // User is a tenant, query the Units node to find their unit
            database.child("Units").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (unitSnapshot in dataSnapshot.children) {
                        val tenantsSnapshot = unitSnapshot.child("tenants")
                        for (tenantSnapshot in tenantsSnapshot.children) {
                            if (tenantSnapshot.value == userId) {
                                val unit = unitSnapshot.key
                                val tenantID = tenantSnapshot.value.toString()
                                // Display the unit details
                                findViewById<TextView>(R.id.unitNumberTextView).text = "$unit"
                                editor.putString("unitNumber", unit)
                                editor.apply()
//                                Toast.makeText(this@Home, "Unit: $unit, Tenant: $tenantID", Toast.LENGTH_SHORT).show()
                                return
                            }
                        }
                    }
                    // Handle case where no unit is found for the tenant
                    Toast.makeText(this@Home, "No unit found for this tenant.", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Home, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun getCurrentLocation(callback: (String) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val sharedPref = getSharedPreferences("my.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val unitNumber = sharedPref.getString("unitNumber", "Unknown")

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val locationMessage = "Emergency! I am owner of Unit No: $unitNumber. My current location: https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                    callback(locationMessage)
                } else {
                    // Request new location data
                    val locationRequest = LocationRequest.create().apply {
                        numUpdates = 1 // Only request a single update
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }

                    fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            fusedLocationClient.removeLocationUpdates(this) // Remove location updates listener

                            val newLocation = locationResult.locations.firstOrNull()
                            if (newLocation != null) {
                                val locationMessage = "Emergency! I am owner of Unit No: $unitNumber. My current location: https://www.google.com/maps/search/?api=1&query=${newLocation.latitude},${newLocation.longitude}"
                                callback(locationMessage)
                            } else {
                                callback("Emergency! Current location not available.")
                            }
                        }
                    }, Looper.getMainLooper())
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun promptEnableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    //request sms location permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ), REQUEST_SMS_LOCATION_PERMISSIONS)
    }

    // override the request permission result (request call phone)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, make the call
                    makePhoneCall()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show()
                }
                return
            }
            REQUEST_SMS_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // All required permissions granted
                    sendEmergencySMS()
                } else {
                    Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
                }
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
        }
    }

}