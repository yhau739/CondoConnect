package com.example.learningandroid

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class AdminHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
    }

    fun onManageBillingClick(view: View) {
        // Handle the click event
        showBillingBottomSheetDialog()
    }

    fun onFeedbackClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, AdminFeedback::class.java))
    }

    fun onClubClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, AdminManageClub::class.java))
    }

    fun onNoticesClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, AdminNoticeActivity::class.java))
    }

    fun onManageDocumentClick(view: View) {
        // Handle the click event
        startActivity(Intent(this, admin_documents::class.java))
    }

    fun onVisitorClick(view: View) {
        // Handle the click event
        showVisitorBottomSheetDialog()
    }

    fun onFacilityClick(view: View) {
        // Handle the click event
        showFacilityBottomSheetDialog()
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

    private fun showBillingBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.admin_billing_bottom_sheet_layout,
            findViewById<LinearLayout>(R.id.billing_bottom_sheet_container)
        )

        // Click listener for back arrow
        bottomSheetView.findViewById<ImageView>(R.id.backArrowSheet).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.approve_payment).setOnClickListener {
            startActivity(Intent(this, AdminApproveBill::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.generate_bill).setOnClickListener {
            startActivity(Intent(this, AdminGenerateBill::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showFacilityBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.admin_facility_bottom_sheet_layout,
            findViewById<LinearLayout>(R.id.facility_bottom_sheet_container)
        )

        // Click listener for back arrow
        bottomSheetView.findViewById<ImageView>(R.id.backArrowSheet).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Click listener for Approve/Cancel
        bottomSheetView.findViewById<LinearLayout>(R.id.check_inout).setOnClickListener {
            // Replace with your desired action
            startActivity(Intent(this, AdminApproveCancelBooking::class.java))
            bottomSheetDialog.dismiss()
        }

        // Click listener for History
        bottomSheetView.findViewById<LinearLayout>(R.id.see_history).setOnClickListener {
            // Replace with your desired action
            startActivity(Intent(this, AdminBookingHistory::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }


    private fun showVisitorBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.admin_visitor_bottom_sheet_layout,
            findViewById<LinearLayout>(R.id.visitor_bottom_sheet_container)
        )

        // Click listener for back arrow
        bottomSheetView.findViewById<ImageView>(R.id.backArrowSheet).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.check_inout).setOnClickListener {
            startActivity(Intent(this, AdminVisitor::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.see_history).setOnClickListener {
            // Handle the click event for see history
            // For example, you can navigate to a history activity
            startActivity(Intent(this, AdminVisitorHistory::class.java))
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }
}