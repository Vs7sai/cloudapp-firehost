package com.v7techsolution.interviewfire

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GoogleAuthHelper(private val activity: Activity) {

    companion object {
        private const val TAG = "GoogleAuthHelper"
        private const val RC_SIGN_IN = 9001
    }

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Callback interface for authentication results
    interface AuthCallback {
        fun onAuthSuccess(user: FirebaseUser?, idToken: String?)
        fun onAuthFailed(error: String)
    }

    private var authCallback: AuthCallback? = null

    init {
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun setAuthCallback(callback: AuthCallback) {
        this.authCallback = callback
    }

    fun startSignIn() {
        Log.d(TAG, "Starting Google Sign-In")
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        Log.d(TAG, "Signing out user")
        auth.signOut()
        googleSignInClient.signOut()
    }
    
    fun clearSignInState() {
        Log.d(TAG, "Clearing sign-in state")
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Google Sign-In state cleared")
        }
    }

    fun handleSignInResult(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                Log.d(TAG, "Google Sign-In successful: ${account.email}")
                firebaseAuthWithGoogle(account)
            } else {
                Log.e(TAG, "Google Sign-In failed: account is null")
                authCallback?.onAuthFailed("Google Sign-In failed")
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed with exception", e)
            Log.e(TAG, "Error code: ${e.statusCode}, Message: ${e.message}")
            val errorMessage = when (e.statusCode) {
                7 -> "Network error - check your internet connection and try again"
                12500 -> "Google Play Services not available"
                12501 -> "Sign-in cancelled by user"
                12502 -> "Sign-in failed"
                12503 -> "Sign-in failed - invalid configuration"
                10 -> "Developer error - check your configuration"
                else -> "Google Sign-In failed (Code: ${e.statusCode}): ${e.message}"
            }
            authCallback?.onAuthFailed(errorMessage)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "Authenticating with Firebase using Google account: ${account.email}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val idToken = account.idToken

                    Log.d(TAG, "Firebase authentication successful: ${user?.email}")
                    Log.d(TAG, "User ID: ${user?.uid}")
                    Log.d(TAG, "ID Token obtained: ${idToken != null}")

                    authCallback?.onAuthSuccess(user, idToken)
                } else {
                    Log.e(TAG, "Firebase authentication failed", task.exception)
                    authCallback?.onAuthFailed("Firebase authentication failed: ${task.exception?.message}")
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getIdToken(callback: (String?, Exception?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.getIdToken(true)
                .addOnSuccessListener { result ->
                    callback(result.token, null)
                }
                .addOnFailureListener { exception ->
                    callback(null, exception)
                }
        } else {
            callback(null, Exception("No user signed in"))
        }
    }
}