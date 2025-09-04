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
import com.v7techsolution.interviewfire.HomeFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

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

            setContentView(R.layout.activity_main)

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
