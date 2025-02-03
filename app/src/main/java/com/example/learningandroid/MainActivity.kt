package com.example.learningandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    // Define a companion object to hold static-like constants
    companion object {
        private const val SPLASH_SCREEN_DURATION: Int = 3000

        // static database instance
        private lateinit var databaseInstance: FirebaseDatabase
        private lateinit var storageInstance: FirebaseStorage

        // getdb() function
        fun getDatabase(): FirebaseDatabase {
            if (!::databaseInstance.isInitialized) {
                databaseInstance = FirebaseDatabase.getInstance("https://learningandroid-1b19c-default-rtdb.asia-southeast1.firebasedatabase.app/")
                // Disable disk persistence
                databaseInstance.setPersistenceEnabled(true)
            }
            return databaseInstance
        }

        fun getStorage(): FirebaseStorage {
            storageInstance = FirebaseStorage.getInstance("gs://learningandroid-1b19c.appspot.com")
            return storageInstance
        }
    }

    // Variables
    lateinit var topAnim: Animation;
    lateinit var bottomAnim: Animation;
    lateinit var image:ImageView;
    lateinit var logo:TextView;
    lateinit var  slogan:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase instance
//        FirebaseDatabase.getInstance("https://learningandroid-1b19c-default-rtdb.asia-southeast1.firebasedatabase.app").setPersistenceEnabled(false)
//        databaseInstance.setPersistenceEnabled(false)

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        image = findViewById(R.id.splash_imageView);
        logo = findViewById(R.id.splash_textView);
        slogan = findViewById(R.id.splash_textView2);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        // Handler to start the Dashboard activity after SPLASH_SCREEN_DURATION milliseconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_DURATION.toLong())
    }
}