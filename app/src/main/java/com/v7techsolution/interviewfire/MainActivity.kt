package com.v7techsolution.interviewfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.ads.MobileAds
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.v7techsolution.interviewfire.HomeFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 3001
    }
    
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate started")

        try {
            // Check authentication first
            if (!isUserAuthenticated()) {
                Log.d(TAG, "User not authenticated, redirecting to LoginActivity")
                redirectToLogin()
                return
            }

            // Hide the default action bar to remove any title
            supportActionBar?.hide()
            
            // Force status bar color to purple
            window.statusBarColor = getColor(R.color.purple_500)
            window.navigationBarColor = getColor(R.color.purple_500)

            setContentView(R.layout.activity_main)

            // Initialize AdMob
            MobileAds.initialize(this) { initializationStatus ->
                Log.d(TAG, "AdMob initialization complete: ${initializationStatus.adapterStatusMap}")
            }

            // Initialize notifications
            notificationHelper = NotificationHelper(this)
            setupNotifications()

            // Setup dynamic navigation handling
            setupDynamicNavigation()

            // Initialize navigation
            initializeNavigation()

            // Load home fragment by default
            if (savedInstanceState == null) {
                loadFragment(HomeFragment())
            }

        } catch (e: Exception) {
            Log.e(TAG, "Critical error in MainActivity onCreate", e)
            Toast.makeText(this, "App initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    // Setup notifications
    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request notification permission for Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                scheduleNotifications()
            }
        } else {
            // For older Android versions, schedule notifications directly
            scheduleNotifications()
        }
    }
    
    // Schedule daily notifications
    private fun scheduleNotifications() {
        try {
            notificationHelper.scheduleAllDailyNotifications()
            Log.d(TAG, "Daily study notifications scheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling notifications", e)
        }
    }
    
    // Handle permission results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Notification permission granted")
                    scheduleNotifications()
                } else {
                    Log.w(TAG, "Notification permission denied")
                    Toast.makeText(this, "Notifications disabled. You won't receive study reminders.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * Setup dynamic navigation handling
     * - If system navigation bar is present (bottomInset > 0), move BottomNavigationView above it
     * - If no system navigation bar (bottomInset == 0), keep at bottom
     */
    private fun setupDynamicNavigation() {
        try {
            Log.d(TAG, "Setting up dynamic navigation handling...")

            val bottomNavigation = findViewById<LinearLayout>(R.id.bottom_navigation)

            if (bottomNavigation != null) {
                Log.d(TAG, "Bottom navigation found, applying dynamic margin...")

                // Use WindowInsets to handle system navigation bar
                ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation) { view, windowInsets ->
                    val systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    val bottomInset = systemBarsInsets.bottom

                    Log.d(TAG, "System bars bottom inset: ${bottomInset}px")

                    val layoutParams = view.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

                    // Apply bottom margin equal to the system navigation bar height
                    // This moves the navigation above the system bar if present, or keeps it at bottom if not
                    layoutParams.bottomMargin = bottomInset

                    view.layoutParams = layoutParams
                    windowInsets
                }

                // Force apply insets
                ViewCompat.requestApplyInsets(bottomNavigation)

                Log.d(TAG, "Dynamic navigation setup completed")

            } else {
                Log.e(TAG, "Bottom navigation view not found")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error setting up navigation", e)
            Toast.makeText(this, "Error setting up navigation: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initializeNavigation() {
        Log.d(TAG, "Initializing navigation...")

        try {
            // Set up navigation button click listeners
            val homeButton = findViewById<LinearLayout>(R.id.nav_home)
            val profileButton = findViewById<LinearLayout>(R.id.nav_profile)

            homeButton?.setOnClickListener {
                loadFragment(HomeFragment())
                updateNavigationSelection(homeButton, profileButton)
            }

            profileButton?.setOnClickListener {
                showLogoutDialog()
            }

            Log.d(TAG, "Navigation initialized successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing navigation", e)
            Toast.makeText(this, "Error initializing navigation: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            Log.d(TAG, "Fragment loaded: ${fragment.javaClass.simpleName}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading fragment", e)
            Toast.makeText(this, "Error loading content: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNavigationSelection(selectedButton: LinearLayout, unselectedButton: LinearLayout?) {
        try {
            // Update selected button appearance
            selectedButton.setBackgroundResource(R.drawable.button_glass_selected)

            // Update unselected button appearance
            unselectedButton?.setBackgroundResource(android.R.color.transparent)

            Log.d(TAG, "Navigation selection updated")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating navigation selection", e)
        }
    }

    private fun isUserAuthenticated(): Boolean {
        return AuthHelper.isUserAuthenticated()
    }

    private fun redirectToLogin() {
        Log.d(TAG, "Redirecting to LoginActivity")
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLogoutDialog() {
        try {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing logout dialog", e)
            Toast.makeText(this, "Error showing logout dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogout() {
        try {
            Log.d(TAG, "Performing logout...")

            // Sign out from Firebase
            AuthHelper.signOut()

            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Redirect to login
            redirectToLogin()

        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            Toast.makeText(this, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Public method for fragments to start topic activity
    fun startTopicActivity(topic: String) {
        try {
            Log.d(TAG, "Starting TopicActivity for topic: $topic")

            if (topic.isBlank()) {
                Log.e(TAG, "Topic parameter is blank")
                Toast.makeText(this, "Invalid topic selected", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(this, TopicActivity::class.java)
            intent.putExtra("topic", topic)
            startActivity(intent)

            Log.d(TAG, "TopicActivity started successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error starting topic activity", e)
            Toast.makeText(this, "Error opening topic: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
