package com.v7techsolution.interviewfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity(), GoogleAuthHelper.AuthCallback {

    private lateinit var googleAuthHelper: GoogleAuthHelper
    private lateinit var signInButton: SignInButton
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the action bar
        supportActionBar?.hide()

        setContentView(R.layout.activity_login)

        // Initialize Google Auth Helper
        googleAuthHelper = GoogleAuthHelper(this)
        googleAuthHelper.setAuthCallback(this)

        // Initialize views
        initializeViews()

        // Check if user is already signed in
        if (googleAuthHelper.isUserSignedIn()) {
            Log.d(TAG, "User already signed in, proceeding to main activity")
            proceedToMainActivity()
            return
        }

        // Setup sign-in button
        setupSignInButton()

        Log.d(TAG, "LoginActivity created and ready for authentication")
    }

    private fun initializeViews() {
        signInButton = findViewById(R.id.sign_in_button)
        statusText = findViewById(R.id.status_text)
        progressBar = findViewById(R.id.progress_bar)

        // Set initial text
        statusText.text = "Sign in to continue"
    }

    private fun setupSignInButton() {
        signInButton.setOnClickListener {
            Log.d(TAG, "Sign-in button clicked")
            statusText.text = "Signing in..."
            progressBar.visibility = android.view.View.VISIBLE
            signInButton.isEnabled = false
            googleAuthHelper.startSignIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) { // RC_SIGN_IN from GoogleAuthHelper
            Log.d(TAG, "Received sign-in result")
            statusText.text = getString(R.string.authenticating_message)
            googleAuthHelper.handleSignInResult(data)
        }
    }

    override fun onAuthSuccess(user: FirebaseUser?, idToken: String?) {
        Log.d(TAG, "Authentication successful for user: ${user?.email}")

        runOnUiThread {
            progressBar.visibility = android.view.View.GONE
            signInButton.isEnabled = true
            statusText.text = "Welcome, ${user?.displayName ?: user?.email ?: "User"}!"

            // Show success message
            Toast.makeText(this, "Welcome, ${user?.displayName ?: user?.email}!", Toast.LENGTH_SHORT).show()

            // Proceed to main activity after a short delay
            android.os.Handler(mainLooper).postDelayed({
                proceedToMainActivity()
            }, 1500)
        }
    }

    override fun onAuthFailed(error: String) {
        Log.e(TAG, "Authentication failed: $error")

        runOnUiThread {
            progressBar.visibility = android.view.View.GONE
            signInButton.isEnabled = true
            statusText.text = "Sign-in failed: $error"
            Toast.makeText(this, "Sign-in failed: $error", Toast.LENGTH_LONG).show()
        }
    }

    private fun proceedToMainActivity() {
        Log.d(TAG, "Proceeding to MainActivity")

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "LoginActivity destroyed")
    }
}